package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentHandler;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipModel;
import org.hisp.dhis.android.core.relationship.RelationshipModelBuilder;
import org.hisp.dhis.android.core.relationship.RelationshipStore;

import java.util.Collection;
import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

public class TrackedEntityInstanceHandler {
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final ObjectWithoutUidStore<RelationshipModel> relationshipStore;
    private final TrackedEntityAttributeValueHandler trackedEntityAttributeValueHandler;
    private final EnrollmentHandler enrollmentHandler;

    public TrackedEntityInstanceHandler(
            @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
            @NonNull ObjectWithoutUidStore relationshipStore,
            @NonNull TrackedEntityAttributeValueHandler trackedEntityAttributeValueHandler,
            @NonNull EnrollmentHandler enrollmentHandler) {
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.relationshipStore = relationshipStore;
        this.trackedEntityAttributeValueHandler = trackedEntityAttributeValueHandler;
        this.enrollmentHandler = enrollmentHandler;
    }

    public void handle(@NonNull TrackedEntityInstance trackedEntityInstance, boolean asRelationship) {
        if (trackedEntityInstance == null) {
            return;
        }

        if (isDeleted(trackedEntityInstance)) {
            trackedEntityInstanceStore.delete(trackedEntityInstance.uid());
        } else {

            if (asRelationship) {
                State currentState = trackedEntityInstanceStore.getState(trackedEntityInstance.uid());

                if (currentState == State.RELATIONSHIP) {
                    updateOrInsert(trackedEntityInstance, State.RELATIONSHIP);
                } else if (currentState == null) {
                    insert(trackedEntityInstance, State.RELATIONSHIP);
                }

            } else {
                updateOrInsert(trackedEntityInstance, State.SYNCED);
            }

            trackedEntityAttributeValueHandler.handle(
                    trackedEntityInstance.uid(),
                    trackedEntityInstance.trackedEntityAttributeValues());

            List<Enrollment> enrollments = trackedEntityInstance.enrollments();

            enrollmentHandler.handle(enrollments);

            RelationshipModelBuilder relationshipModelBuilder = new RelationshipModelBuilder();
            for (Relationship relationship : trackedEntityInstance.relationships()) {
                this.handle(relationship.relative(), true);
                this.relationshipStore.updateOrInsertWhere(relationshipModelBuilder.buildModel(relationship));
            }
        }
    }

    private void updateOrInsert(@NonNull TrackedEntityInstance trackedEntityInstance, State state) {
        int affectedRows = trackedEntityInstanceStore.update(
                trackedEntityInstance.uid(), trackedEntityInstance.created(),
                trackedEntityInstance.lastUpdated(), trackedEntityInstance.createdAtClient(),
                trackedEntityInstance.lastUpdatedAtClient(), trackedEntityInstance.organisationUnit(),
                trackedEntityInstance.trackedEntityType(), trackedEntityInstance.coordinates(),
                trackedEntityInstance.featureType(), state, trackedEntityInstance.uid());
        if (affectedRows <= 0) {
            insert(trackedEntityInstance, state);
        }
    }

    private void insert(@NonNull TrackedEntityInstance trackedEntityInstance, State state) {
        trackedEntityInstanceStore.insert(
                trackedEntityInstance.uid(), trackedEntityInstance.created(),
                trackedEntityInstance.lastUpdated(), trackedEntityInstance.createdAtClient(),
                trackedEntityInstance.lastUpdatedAtClient(), trackedEntityInstance.organisationUnit(),
                trackedEntityInstance.trackedEntityType(), trackedEntityInstance.coordinates(),
                trackedEntityInstance.featureType(), state);
    }

    public void handleMany(@NonNull Collection<TrackedEntityInstance> trackedEntityInstances) {
        for (TrackedEntityInstance trackedEntityInstance : trackedEntityInstances) {
            handle(trackedEntityInstance, false);
        }
    }

    public static TrackedEntityInstanceHandler create(DatabaseAdapter databaseAdapter) {
        return new TrackedEntityInstanceHandler(
                new TrackedEntityInstanceStoreImpl(databaseAdapter),
                RelationshipStore.create(databaseAdapter),
                TrackedEntityAttributeValueHandler.create(databaseAdapter),
                EnrollmentHandler.create(databaseAdapter)
        );
    }
}
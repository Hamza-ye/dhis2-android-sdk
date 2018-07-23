package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentHandler;
import org.hisp.dhis.android.core.period.FeatureType;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class TrackedEntityInstanceHandlerShould {
    @Mock
    private TrackedEntityInstanceStore trackedEntityInstanceStore;

    @Mock
    private ObjectWithoutUidStore<RelationshipModel> relationshipStore;

    @Mock
    private TrackedEntityAttributeValueHandler trackedEntityAttributeValueHandler;

    @Mock
    private EnrollmentHandler enrollmentHandler;

    @Mock
    private TrackedEntityInstance trackedEntityInstance;

    @Mock
    private Enrollment enrollment;

    @Mock
    private Relationship relationship;

    // object to test
    private TrackedEntityInstanceHandler trackedEntityInstanceHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(trackedEntityInstance.uid()).thenReturn("test_tei_uid");
        when(trackedEntityInstance.enrollments()).thenReturn(Collections.singletonList(enrollment));
        when(trackedEntityInstance.relationships()).thenReturn(Collections.singletonList(relationship));

        trackedEntityInstanceHandler = new TrackedEntityInstanceHandler(
                trackedEntityInstanceStore, relationshipStore, trackedEntityAttributeValueHandler, enrollmentHandler
        );

    }

    @Test
    public void do_nothing_when_passing_null_argument() throws Exception {
        trackedEntityInstanceHandler.handle(null, false);

        // verify that tracked entity instance store is never called
        verify(trackedEntityInstanceStore, never()).delete(anyString());
        verify(trackedEntityInstanceStore, never()).update(anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(), anyString(), anyString(), anyString(), any(FeatureType.class),
                any(State.class), anyString());
        verify(trackedEntityInstanceStore, never()).insert(anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(), anyString(), anyString(), anyString(), any(FeatureType.class),
                any(State.class));
        verify(trackedEntityAttributeValueHandler, never()).handle(any(String.class), any(ArrayList.class));
        verify(enrollmentHandler, never()).handle(any(ArrayList.class));
    }

    @Test
    public void invoke_delete_when_handle_program_tracked_entity_instance_set_as_deleted() throws Exception {
        when(trackedEntityInstance.deleted()).thenReturn(Boolean.TRUE);

        trackedEntityInstanceHandler.handle(trackedEntityInstance, false);

        // verify that tracked entity instance store is only called with delete
        verify(trackedEntityInstanceStore, times(1)).delete(anyString());

        verify(trackedEntityInstanceStore, never()).update(anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(), anyString(), anyString(), anyString(), any(FeatureType.class),
                any(State.class), anyString());
        verify(trackedEntityInstanceStore, never()).insert(anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(), anyString(), anyString(), anyString(), any(FeatureType.class),
                any(State.class));

        verify(trackedEntityAttributeValueHandler, never()).handle(
                any(String.class), any(ArrayList.class));

        // verify that enrollment handler is never called
        verify(enrollmentHandler, never()).handle(any(ArrayList.class));
    }

    @Test
    public void invoke_only_update_when_handle_tracked_entity_instance_inserted() throws Exception {
        when(trackedEntityInstanceStore.update(anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(), anyString(), anyString(), anyString(), any(FeatureType.class),
                any(State.class), anyString())).thenReturn(1);

        trackedEntityInstanceHandler.handle(trackedEntityInstance, false);


        // verify that tracked entity instance store is only called with update
        verify(trackedEntityInstanceStore, times(1)).update(anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(), anyString(), anyString(), anyString(), any(FeatureType.class),
                any(State.class), anyString());


        verify(trackedEntityInstanceStore, never()).insert(anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(),anyString(), anyString(), anyString(), any(FeatureType.class),
                any(State.class));
        verify(trackedEntityInstanceStore, never()).delete(anyString());

        verify(trackedEntityAttributeValueHandler, times(1)).handle(
                any(String.class), any(ArrayList.class));

        // verify that enrollment handler is called once
        verify(enrollmentHandler, times(1)).handle(any(ArrayList.class));

    }

    @Test
    public void invoke_update_and_insert_when_handle_tracked_entity_instance_not_inserted() throws Exception {
        when(trackedEntityInstanceStore.update(anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(), anyString(), anyString(), anyString(), any(FeatureType.class),
                any(State.class), anyString())).thenReturn(0);

        trackedEntityInstanceHandler.handle(trackedEntityInstance, false);

        // verify that tracked entity instance store is called with insert
        verify(trackedEntityInstanceStore, times(1)).insert(anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(),anyString(), anyString(), anyString(), any(FeatureType.class),
                any(State.class));

        // update is also invoked since we're trying to update before we insert

        verify(trackedEntityInstanceStore, times(1)).update(anyString(), any(Date.class), any(Date.class),
                anyString(), anyString(), anyString(), anyString(), anyString(), any(FeatureType.class),
                any(State.class), anyString());

        // check that delete is never invoked
        verify(trackedEntityInstanceStore, never()).delete(anyString());

        verify(trackedEntityAttributeValueHandler, times(1)).handle(
                any(String.class), any(ArrayList.class));

        // verify that enrollment handler is called once
        verify(enrollmentHandler, times(1)).handle(any(ArrayList.class));
    }

    @Test
    public void invoke_relationship_store_insert_when_relationship_exists() throws Exception {
        trackedEntityInstanceHandler.handle(trackedEntityInstance, false);

        // verify relationship store is called once
        verify(relationshipStore, times(1)).updateOrInsertWhere(any(RelationshipModel.class));
    }
}
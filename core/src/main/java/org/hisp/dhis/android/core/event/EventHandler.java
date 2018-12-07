package org.hisp.dhis.android.core.event;

import android.util.Log;

import org.hisp.dhis.android.core.arch.handlers.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.ModelBuilder;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueHandler;

public class EventHandler extends IdentifiableSyncHandlerImpl<Event> {
    private final TrackedEntityDataValueHandler trackedEntityDataValueHandler;

    EventHandler(EventStore eventStore, TrackedEntityDataValueHandler trackedEntityDataValueHandler) {
        super(eventStore);
        this.trackedEntityDataValueHandler = trackedEntityDataValueHandler;
    }

    @Override
    protected void afterObjectHandled(Event event, HandleAction action) {
        final String eventUid = event.uid();
        trackedEntityDataValueHandler.handleMany(event.trackedEntityDataValues(),
                new ModelBuilder<TrackedEntityDataValue, TrackedEntityDataValue>() {
                    @Override
                    public TrackedEntityDataValue buildModel(TrackedEntityDataValue dataValue) {
                        return dataValue.toBuilder().event(eventUid).build();
                    }
                });

        if (action == HandleAction.Delete) {
            Log.d(this.getClass().getSimpleName(), eventUid + " with no org. unit, invalid eventDate or deleted");
        }
    }

    @Override
    protected boolean deleteIfCondition(Event event) {
        Boolean validEventDate = event.eventDate() != null ||
                event.status() == EventStatus.SCHEDULE ||
                event.status() == EventStatus.SKIPPED ||
                event.status() == EventStatus.OVERDUE;

        return !validEventDate || event.organisationUnit() == null;
    }

    public static SyncHandler<Event> create(DatabaseAdapter databaseAdapter) {
        return new EventHandler(
                EventStoreImpl.create(databaseAdapter),
                TrackedEntityDataValueHandler.create(databaseAdapter)
        );
    }
}
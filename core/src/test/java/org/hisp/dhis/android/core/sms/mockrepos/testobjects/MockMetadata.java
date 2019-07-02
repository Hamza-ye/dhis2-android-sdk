package org.hisp.dhis.android.core.sms.mockrepos.testobjects;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.smscompression.models.SMSMetadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MockMetadata extends SMSMetadata {
    public List<String> getUsers() {
        return Collections.singletonList(MockObjects.user);
    }

    public List<String> getTrackedEntityTypes() {
        return Collections.singletonList(MockObjects.teiUid);
    }

    public List<String> getTrackedEntityAttributes() {
        ArrayList<String> attrs = new ArrayList<>();
        for (TrackedEntityAttributeValue item : MockObjects.getTestValues()) {
            attrs.add(item.trackedEntityAttribute());
        }
        return attrs;
    }

    public List<String> getPrograms() {
        return Collections.singletonList(MockObjects.program);
    }

    public List<String> getOrganisationUnits() {
        return Collections.singletonList(MockObjects.orgUnit);
    }
}
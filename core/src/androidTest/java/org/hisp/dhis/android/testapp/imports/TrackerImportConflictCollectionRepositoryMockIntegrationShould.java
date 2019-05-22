/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.testapp.imports;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.SyncedDatabaseMockIntegrationShould;
import org.hisp.dhis.android.core.imports.ImportStatus;
import org.hisp.dhis.android.core.imports.TrackerImportConflict;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.List;

import androidx.test.runner.AndroidJUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class TrackerImportConflictCollectionRepositoryMockIntegrationShould
        extends SyncedDatabaseMockIntegrationShould {

    @Test
    public void find_all() {
        List<TrackerImportConflict> trackerImportConflicts = d2.importModule().trackerImportConflicts.get();
        assertThat(trackerImportConflicts.size(), is(2));
    }

    @Test
    public void filter_by_conflict() {
        List<TrackerImportConflict> trackerImportConflicts = d2.importModule().trackerImportConflicts
                .byConflict().eq("conflict").get();
        assertThat(trackerImportConflicts.size(), is(1));
    }

    @Test
    public void filter_by_value() {
        List<TrackerImportConflict> trackerImportConflicts = d2.importModule().trackerImportConflicts
                .byValue().eq("value").get();
        assertThat(trackerImportConflicts.size(), is(1));
    }

    @Test
    public void filter_by_tracked_entity_instance() {
        List<TrackerImportConflict> trackerImportConflicts = d2.importModule().trackerImportConflicts
                .byTrackedEntityInstanceUid().eq("nWrB0TfWlvh").get();
        assertThat(trackerImportConflicts.size(), is(1));
    }

    @Test
    public void filter_by_enrollment() {
        List<TrackerImportConflict> trackerImportConflicts = d2.importModule().trackerImportConflicts
                .byEnrollmentUid().eq("enroll2").get();
        assertThat(trackerImportConflicts.size(), is(1));
    }

    @Test
    public void filter_by_event() {
        List<TrackerImportConflict> trackerImportConflicts = d2.importModule().trackerImportConflicts
                .byEventUid().eq("event2").get();
        assertThat(trackerImportConflicts.size(), is(1));
    }

    @Test
    public void filter_by_tale_reference() {
        List<TrackerImportConflict> trackerImportConflicts = d2.importModule().trackerImportConflicts
                .byTableReference().eq("table_reference").get();
        assertThat(trackerImportConflicts.size(), is(1));
    }

    @Test
    public void filter_by_error_code() {
        List<TrackerImportConflict> trackerImportConflicts = d2.importModule().trackerImportConflicts
                .byErrorCode().eq("error_code").get();
        assertThat(trackerImportConflicts.size(), is(1));
    }

    @Test
    public void filter_by_status() {
        List<TrackerImportConflict> trackerImportConflicts = d2.importModule().trackerImportConflicts
                .byStatus().eq(ImportStatus.SUCCESS).get();
        assertThat(trackerImportConflicts.size(), is(1));
    }

    @Test
    public void filter_by_created() throws ParseException {
        List<TrackerImportConflict> trackerImportConflicts = d2.importModule().trackerImportConflicts
                .byCreated().eq(BaseIdentifiableObject.parseDate("2017-11-29T11:27:46.935")).get();
        assertThat(trackerImportConflicts.size(), is(2));
    }
}
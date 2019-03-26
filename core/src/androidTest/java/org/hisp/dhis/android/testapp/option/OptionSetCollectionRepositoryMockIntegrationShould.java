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

package org.hisp.dhis.android.testapp.option;

import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.SyncedDatabaseMockIntegrationShould;
import org.hisp.dhis.android.core.option.OptionSet;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.test.runner.AndroidJUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class OptionSetCollectionRepositoryMockIntegrationShould extends SyncedDatabaseMockIntegrationShould {

    @Test
    public void find_all() {
        List<OptionSet> optionSets = d2.optionModule().optionSets.get();
        assertThat(optionSets.size(), is(1));
    }

    @Test
    public void filter_by_version() {
        List<OptionSet> optionSets = d2.optionModule().optionSets
                .byVersion().eq(1).get();
        assertThat(optionSets.size(), is(1));
    }

    @Test
    public void filter_by_path() {
        List<OptionSet> optionSets = d2.optionModule().optionSets
                .byValueType().eq(ValueType.TEXT).get();
        assertThat(optionSets.size(), is(1));
    }

    @Test
    public void include_options_as_children() {
        OptionSet optionSet = d2.optionModule().optionSets
                .withOptions().one().get();
        assertThat(optionSet.options().get(0).name(), is("0-14 years"));
    }

    @Test
    public void include_options_as_children_in_collection_repository_when_all_selected() {
        OptionSet optionSet = d2.optionModule().optionSets
                .withAllChildren().get().get(0);
        assertThat(optionSet.options().get(0).name(), is("0-14 years"));
    }

    @Test
    public void include_options_as_children_in_object_repository_when_all_selected() {
        OptionSet optionSet = d2.optionModule().optionSets
                .one().withAllChildren().get();
        assertThat(optionSet.options().get(0).name(), is("0-14 years"));
    }
}
/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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

 package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.program.ProgramRuleModel.Columns;
import org.hisp.dhis.android.core.relationship.CreateRelationshipTypeUtils;
import org.hisp.dhis.android.core.relationship.RelationshipTypeModel;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityUtils;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class ProgramRuleModelStoreIntegrationTest extends AbsStoreTestCase {

    private static final String[] PROGRAM_RULE_MODEL_PROJECTION = {
            Columns.UID, Columns.CODE, Columns.NAME, Columns.DISPLAY_NAME,
            Columns.CREATED, Columns.LAST_UPDATED, Columns.PRIORITY, Columns.CONDITION,
            Columns.PROGRAM, Columns.PROGRAM_STAGE
    };

    private ProgramRuleModelStore programRuleStore;

    private static final Long ID = 1L;

    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    // timestamp
    private static final String DATE = "2017-01-11T13:48:00.000";

    // bound to Program Rule
    private static final String PROGRAM_STAGE = "test_programStage";
    private static final String PROGRAM = "test_program";
    private static final Integer PRIORITY = 2;
    private static final String CONDITION = "test_condition";

    //foreign keys to program:
    private static final long TRACKED_ENTITY_ID = 1L;
    private static final String TRACKED_ENTITY_UID = "trackedEntityUid";
    private static final long RELATIONSHIP_TYPE_ID = 3L;
    private static final String RELATIONSHIP_TYPE_UID = "relationshipTypeUid";

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        this.programRuleStore = new ProgramRuleModelStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistProgramRuleInDatabase() throws ParseException {
        //Create Program & insert a row in the table.
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID, TRACKED_ENTITY_UID);
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE_UID);
        ContentValues program = CreateProgramUtils.create(1L, PROGRAM, RELATIONSHIP_TYPE_UID, null, TRACKED_ENTITY_UID);

        database().insert(TrackedEntityModel.TABLE, null, trackedEntity);
        database().insert(RelationshipTypeModel.TABLE, null, relationshipType);
        database().insert(ProgramModel.TABLE, null, program);

        ContentValues programStage = CreateProgramStageUtils.create(1L, PROGRAM_STAGE, PROGRAM);
        database().insert(ProgramStageModel.TABLE, null, programStage);

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        long rowId = programRuleStore.insert(
                UID, CODE, NAME, DISPLAY_NAME,
                timeStamp, timeStamp, PRIORITY,
                CONDITION, PROGRAM, PROGRAM_STAGE);

        Cursor cursor = database().query(ProgramRuleModel.TABLE, PROGRAM_RULE_MODEL_PROJECTION,
                null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);

        assertThatCursor(cursor).hasRow(
                UID, CODE,
                NAME,
                DISPLAY_NAME,
                DATE, DATE,
                PRIORITY, CONDITION,
                PROGRAM, PROGRAM_STAGE
        ).isExhausted();
    }

    @Test
    public void insert_shouldPersistProgramRuleInDatabaseWithoutProgramStageForeignKey() throws ParseException {
        //Create Program & insert a row in the table.
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(TRACKED_ENTITY_ID, TRACKED_ENTITY_UID);
        ContentValues relationshipType = CreateRelationshipTypeUtils.create(RELATIONSHIP_TYPE_ID,
                RELATIONSHIP_TYPE_UID);
        ContentValues program = CreateProgramUtils.create(1L, PROGRAM, RELATIONSHIP_TYPE_UID, null, TRACKED_ENTITY_UID);

        database().insert(TrackedEntityModel.TABLE, null, trackedEntity);
        database().insert(RelationshipTypeModel.TABLE, null, relationshipType);
        database().insert(ProgramModel.TABLE, null, program);

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        long rowId = programRuleStore.insert(
                UID, CODE, NAME, DISPLAY_NAME,
                timeStamp, timeStamp, PRIORITY,
                CONDITION, PROGRAM, null);

        Cursor cursor = database().query(ProgramRuleModel.TABLE, PROGRAM_RULE_MODEL_PROJECTION,
                null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);

        assertThatCursor(cursor).hasRow(
                UID, CODE,
                NAME,
                DISPLAY_NAME,
                DATE, DATE,
                PRIORITY, CONDITION,
                PROGRAM, null
        ).isExhausted();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void exception_shouldNotPersistProgramRuleInDatabaseWithoutProgram() throws ParseException {
        String wrongProgramUid = "wrong";
        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        programRuleStore.insert(UID, CODE, NAME, DISPLAY_NAME, timeStamp, timeStamp, PRIORITY, CONDITION,
                wrongProgramUid, null);
    }

    @Test
    public void delete_shouldDeleteProgramRuleWhenDeletingProgram() throws Exception {
        ContentValues program = CreateProgramUtils.create(ID, PROGRAM, null, null, null);
        database().insert(ProgramModel.TABLE, null, program);

        ContentValues programRule = new ContentValues();
        programRule.put(Columns.ID, ID);
        programRule.put(Columns.UID, UID);
        programRule.put(Columns.PROGRAM, PROGRAM);

        database().insert(ProgramRuleModel.TABLE, null, programRule);

        String[] projection = {Columns.ID, Columns.UID, Columns.PROGRAM};
        Cursor cursor = database().query(ProgramRuleModel.TABLE, projection, null, null, null, null, null);
        // checking that program rule was successfully inserted into database
        assertThatCursor(cursor).hasRow(ID, UID, PROGRAM);

        database().delete(ProgramModel.TABLE, ProgramModel.Columns.UID + " =?", new String[]{PROGRAM});

        cursor = database().query(ProgramRuleModel.TABLE, projection, null, null, null, null, null);

        assertThatCursor(cursor).isExhausted();

    }

    @Test
    public void delete_shouldDeleteProgramRuleWhenDeletingProgramStage() throws Exception {
        ContentValues program = CreateProgramUtils.create(ID, PROGRAM, null, null, null);
        database().insert(ProgramModel.TABLE, null, program);

        ContentValues programStage = CreateProgramStageUtils.create(ID, PROGRAM_STAGE, PROGRAM);
        database().insert(ProgramStageModel.TABLE, null, programStage);

        ContentValues programRule = new ContentValues();
        programRule.put(Columns.ID, ID);
        programRule.put(Columns.UID, UID);
        programRule.put(Columns.PROGRAM, PROGRAM);
        programRule.put(Columns.PROGRAM_STAGE, PROGRAM_STAGE);

        database().insert(ProgramRuleModel.TABLE, null, programRule);

        String[] projection = {Columns.ID, Columns.UID, Columns.PROGRAM, Columns.PROGRAM_STAGE};
        Cursor cursor = database().query(ProgramRuleModel.TABLE, projection, null, null, null, null, null);
        // checking that program rule is successfully inserted
        assertThatCursor(cursor).hasRow(ID, UID, PROGRAM, PROGRAM_STAGE);

        database().delete(ProgramStageModel.TABLE, ProgramStageModel.Columns.UID + " =?", new String[]{PROGRAM_STAGE});

        cursor = database().query(ProgramRuleModel.TABLE, projection, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();

    }

    @Test
    public void close_shouldNotCloseDatabase() {
        programRuleStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
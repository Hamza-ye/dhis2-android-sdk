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

package org.hisp.dhis.android.core.configuration.internal;

import android.database.Cursor;
import android.util.Log;

import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor;
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.cursors.internal.ObjectFactory;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore;
import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCategoryComboLink;
import org.hisp.dhis.android.core.category.CategoryCategoryComboLinkTableInfo;
import org.hisp.dhis.android.core.category.CategoryCategoryOptionLink;
import org.hisp.dhis.android.core.category.CategoryCategoryOptionLinkTableInfo;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboTableInfo;
import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.category.CategoryOptionComboCategoryOptionLink;
import org.hisp.dhis.android.core.category.CategoryOptionComboCategoryOptionLinkTableInfo;
import org.hisp.dhis.android.core.category.CategoryOptionComboTableInfo;
import org.hisp.dhis.android.core.category.CategoryOptionTableInfo;
import org.hisp.dhis.android.core.category.CategoryTableInfo;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.common.ValueTypeDeviceRendering;
import org.hisp.dhis.android.core.common.ValueTypeDeviceRenderingTableInfo;
import org.hisp.dhis.android.core.constant.Constant;
import org.hisp.dhis.android.core.constant.ConstantTableInfo;
import org.hisp.dhis.android.core.dataapproval.DataApproval;
import org.hisp.dhis.android.core.dataapproval.DataApprovalTableInfo;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementOperand;
import org.hisp.dhis.android.core.dataelement.DataElementOperandTableInfo;
import org.hisp.dhis.android.core.dataelement.DataElementTableInfo;
import org.hisp.dhis.android.core.dataset.DataInputPeriod;
import org.hisp.dhis.android.core.dataset.DataInputPeriodTableInfo;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistrationTableInfo;
import org.hisp.dhis.android.core.dataset.DataSetCompulsoryDataElementOperandLink;
import org.hisp.dhis.android.core.dataset.DataSetCompulsoryDataElementOperandLinkTableInfo;
import org.hisp.dhis.android.core.dataset.DataSetDataElementLinkTableInfo;
import org.hisp.dhis.android.core.dataset.DataSetElement;
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLink;
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLinkTableInfo;
import org.hisp.dhis.android.core.dataset.DataSetTableInfo;
import org.hisp.dhis.android.core.dataset.Section;
import org.hisp.dhis.android.core.dataset.SectionDataElementLink;
import org.hisp.dhis.android.core.dataset.SectionDataElementLinkTableInfo;
import org.hisp.dhis.android.core.dataset.SectionGreyedFieldsLink;
import org.hisp.dhis.android.core.dataset.SectionGreyedFieldsLinkTableInfo;
import org.hisp.dhis.android.core.dataset.SectionTableInfo;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.datavalue.DataValueTableInfo;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventTableInfo;
import org.hisp.dhis.android.core.fileresource.FileResource;
import org.hisp.dhis.android.core.fileresource.FileResourceTableInfo;
import org.hisp.dhis.android.core.imports.TrackerImportConflict;
import org.hisp.dhis.android.core.imports.TrackerImportConflictTableInfo;
import org.hisp.dhis.android.core.indicator.DataSetIndicatorLink;
import org.hisp.dhis.android.core.indicator.DataSetIndicatorLinkTableInfo;
import org.hisp.dhis.android.core.indicator.Indicator;
import org.hisp.dhis.android.core.indicator.IndicatorTableInfo;
import org.hisp.dhis.android.core.indicator.IndicatorType;
import org.hisp.dhis.android.core.indicator.IndicatorTypeTableInfo;
import org.hisp.dhis.android.core.legendset.Legend;
import org.hisp.dhis.android.core.legendset.LegendSet;
import org.hisp.dhis.android.core.legendset.LegendSetTableInfo;
import org.hisp.dhis.android.core.legendset.LegendTableInfo;
import org.hisp.dhis.android.core.legendset.ProgramIndicatorLegendSetLink;
import org.hisp.dhis.android.core.legendset.ProgramIndicatorLegendSetLinkTableInfo;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorTableInfo;
import org.hisp.dhis.android.core.maintenance.ForeignKeyViolation;
import org.hisp.dhis.android.core.maintenance.ForeignKeyViolationTableInfo;
import org.hisp.dhis.android.core.note.Note;
import org.hisp.dhis.android.core.note.NoteTableInfo;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.option.OptionGroup;
import org.hisp.dhis.android.core.option.OptionGroupOptionLink;
import org.hisp.dhis.android.core.option.OptionGroupOptionLinkTableInfo;
import org.hisp.dhis.android.core.option.OptionGroupTableInfo;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.OptionSetTableInfo;
import org.hisp.dhis.android.core.option.OptionTableInfo;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroupTableInfo;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitLevelTableInfo;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitOrganisationUnitGroupLink;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitOrganisationUnitGroupLinkTableInfo;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLink;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkTableInfo;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitTableInfo;
import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.period.PeriodTableInfo;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.hisp.dhis.android.core.program.ProgramIndicatorTableInfo;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.hisp.dhis.android.core.program.ProgramRuleAction;
import org.hisp.dhis.android.core.program.ProgramRuleActionTableInfo;
import org.hisp.dhis.android.core.program.ProgramRuleTableInfo;
import org.hisp.dhis.android.core.program.ProgramRuleVariable;
import org.hisp.dhis.android.core.program.ProgramRuleVariableTableInfo;
import org.hisp.dhis.android.core.program.ProgramSection;
import org.hisp.dhis.android.core.program.ProgramSectionAttributeLink;
import org.hisp.dhis.android.core.program.ProgramSectionAttributeLinkTableInfo;
import org.hisp.dhis.android.core.program.ProgramSectionTableInfo;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.program.ProgramStageDataElementTableInfo;
import org.hisp.dhis.android.core.program.ProgramStageSection;
import org.hisp.dhis.android.core.program.ProgramStageSectionDataElementLink;
import org.hisp.dhis.android.core.program.ProgramStageSectionDataElementLinkTableInfo;
import org.hisp.dhis.android.core.program.ProgramStageSectionProgramIndicatorLink;
import org.hisp.dhis.android.core.program.ProgramStageSectionProgramIndicatorLinkTableInfo;
import org.hisp.dhis.android.core.program.ProgramStageSectionTableInfo;
import org.hisp.dhis.android.core.program.ProgramStageTableInfo;
import org.hisp.dhis.android.core.program.ProgramTableInfo;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeTableInfo;
import org.hisp.dhis.android.core.program.internal.ProgramOrganisationUnitLastUpdated;
import org.hisp.dhis.android.core.program.internal.ProgramOrganisationUnitLastUpdatedTableInfo;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipConstraint;
import org.hisp.dhis.android.core.relationship.RelationshipConstraintTableInfo;
import org.hisp.dhis.android.core.relationship.RelationshipItem;
import org.hisp.dhis.android.core.relationship.RelationshipItemTableInfo;
import org.hisp.dhis.android.core.relationship.RelationshipTableInfo;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.relationship.RelationshipTypeTableInfo;
import org.hisp.dhis.android.core.resource.internal.Resource;
import org.hisp.dhis.android.core.resource.internal.ResourceTableInfo;
import org.hisp.dhis.android.core.settings.DataSetSetting;
import org.hisp.dhis.android.core.settings.DataSetSettingTableInfo;
import org.hisp.dhis.android.core.settings.GeneralSettingTableInfo;
import org.hisp.dhis.android.core.settings.GeneralSettings;
import org.hisp.dhis.android.core.settings.ProgramSetting;
import org.hisp.dhis.android.core.settings.ProgramSettingTableInfo;
import org.hisp.dhis.android.core.settings.SystemSetting;
import org.hisp.dhis.android.core.settings.SystemSettingTableInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.SystemInfoTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValueTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeAttributeTableInfo;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeTableInfo;
import org.hisp.dhis.android.core.user.AuthenticatedUser;
import org.hisp.dhis.android.core.user.AuthenticatedUserTableInfo;
import org.hisp.dhis.android.core.user.Authority;
import org.hisp.dhis.android.core.user.AuthorityTableInfo;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.UserCredentialsTableInfo;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLink;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkTableInfo;
import org.hisp.dhis.android.core.user.UserRole;
import org.hisp.dhis.android.core.user.UserRoleTableInfo;
import org.hisp.dhis.android.core.user.UserTableInfo;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Observable;

@SuppressWarnings("PMD.ExcessiveImports")
@Reusable
class DatabaseCopy {

    private final RxAPICallExecutor executor;

    private final ObjectStore<User> userStore;

    private DatabaseAdapter adapterFrom;
    private DatabaseAdapter adapterTo;


    @Inject
    DatabaseCopy(RxAPICallExecutor executor, IdentifiableObjectStore<User> userStore) {
        this.executor = executor;
        this.userStore = userStore;
    }

    void encrypt(DatabaseAdapter plainTextDatabaseOpenedWithSQLCipher) {
        executor.wrapObservableTransactionally(Observable.fromCallable(() -> {
            long startMillis = System.currentTimeMillis();

            plainTextDatabaseOpenedWithSQLCipher.rawExecSQL("ATTACH DATABASE 'encrypted.db' AS encrypted KEY 'testkey';");
            plainTextDatabaseOpenedWithSQLCipher.rawExecSQL("SELECT sqlcipher_export('encrypted');");
            plainTextDatabaseOpenedWithSQLCipher.rawExecSQL("DETACH DATABASE encrypted");

            long endMillis = System.currentTimeMillis();

            Log.e("COPY", "" + (endMillis - startMillis));

            return new Unit();
        }), true).blockingSubscribe();
    }

    void decrypt(DatabaseAdapter encryptedDatabaseOpenedWithSQLCipher) {
        executor.wrapObservableTransactionally(Observable.fromCallable(() -> {
            long startMillis = System.currentTimeMillis();

            encryptedDatabaseOpenedWithSQLCipher.rawExecSQL("ATTACH DATABASE 'plaintext.db' AS plaintext KEY '';");
            encryptedDatabaseOpenedWithSQLCipher.rawExecSQL("SELECT sqlcipher_export('plaintext');");
            encryptedDatabaseOpenedWithSQLCipher.rawExecSQL("DETACH DATABASE plaintext;");

            long endMillis = System.currentTimeMillis();

            Log.e("COPY", "" + (endMillis - startMillis));

            return new Unit();
        }), true).blockingSubscribe();
    }

    void copyDatabase(DatabaseAdapter adapterFrom, DatabaseAdapter adapterTo) {
        executor.wrapObservableTransactionally(Observable.fromCallable(() -> {
            copyDatabaseInternal(adapterFrom, adapterTo);
            return new Unit();
        }), true).blockingSubscribe();
    }

    private void copyDatabaseInternal(DatabaseAdapter adapterFrom, DatabaseAdapter adapterTo) {
        this.adapterFrom = adapterFrom;
        this.adapterTo = adapterTo;

        long startMillis = System.currentTimeMillis();

        adapterTo.execSQL("ATTACH DATABASE 'encrypted.db' AS encrypted KEY 'testkey';");
        adapterTo.execSQL("SELECT sqlcipher_export('encrypted');");
        adapterTo.execSQL("DETACH DATABASE encrypted");

        long endMillis = System.currentTimeMillis();

        Log.e("COPY", "" + (endMillis - startMillis));

    }

    private <O extends CoreObject> void copyTable(TableInfo tableInfo, ObjectFactory<O> objectFactory) {
        try (Cursor cursor = adapterFrom.rawQuery("SELECT * FROM " + tableInfo.name())) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    adapterTo.insert(tableInfo.name(), null,
                            objectFactory.fromCursor(cursor).toContentValues());
                }
                while (cursor.moveToNext());
            }
        }
    }

    private <O extends CoreObject> void copyTable(TableInfo tableInfo, ObjectFactory<O> objectFactory, ObjectStore<O> store) {
        try (Cursor cursor = adapterFrom.rawQuery("SELECT * FROM " + tableInfo.name())) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    store.insert(objectFactory.fromCursor(cursor));
                }
                while (cursor.moveToNext());
            }
        }
    }
}
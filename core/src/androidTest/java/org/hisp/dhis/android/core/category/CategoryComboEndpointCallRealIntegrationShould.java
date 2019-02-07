package org.hisp.dhis.android.core.category;


import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.LinkModelStore;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.junit.Before;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class CategoryComboEndpointCallRealIntegrationShould extends AbsStoreTestCase {

    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }

    //@Test
    public void download_categories_combos_and_relatives() throws Exception {
        d2.userModule().logIn(RealServerMother.user, RealServerMother.password).call();

        d2.databaseAdapter().database().setForeignKeyConstraintsEnabled(false);

        assertNotCombosInDB();
        assertTrue(getCategoryCategoryComboLinkModels().isEmpty());

        Callable<List<CategoryCombo>> categoryComboEndpointCall =
                getD2DIComponent(d2).internalModules().category.categoryComboCallFactory.create(
                        new HashSet<>(Lists.newArrayList("bjDvmb4bfuf")));
        List<CategoryCombo> categoryCombos = categoryComboEndpointCall.call();

        assertFalse(categoryCombos.isEmpty());

        downloadCategories();

        assertDataIsProperlyParsedAndInsertedInTheDB();
    }

    private void assertDataIsProperlyParsedAndInsertedInTheDB() {
        assertThereAreCombosInDB();
        assertFalse(getCategoryCategoryComboLinkModels().isEmpty());
        assertThereAreCategoryOptionCombosInDB();
        assertThereAreCategoriesInDB();
    }

    private void downloadCategories() throws Exception {
        getD2DIComponent(d2).internalModules().category.categoryCallFactory.create(
                new HashSet<>(Lists.newArrayList("GLevLNI9wkl"))).call();
    }

    private void assertNotCombosInDB() {
        IdentifiableObjectStore<CategoryCombo> categoryComboStore = CategoryComboStore.create(databaseAdapter());
        List<CategoryCombo> categoryCombos = categoryComboStore.selectAll();
        assertTrue(categoryCombos.isEmpty());
    }

    private void assertThereAreCombosInDB() {
        IdentifiableObjectStore<CategoryCombo> categoryComboStore = CategoryComboStore.create(databaseAdapter());
        List<CategoryCombo> categoryCombos = categoryComboStore.selectAll();
        assertTrue(categoryCombos.size() > 0);
    }

    private List<CategoryCategoryComboLinkModel> getCategoryCategoryComboLinkModels() {
        LinkModelStore<CategoryCategoryComboLinkModel>
                categoryCategoryComboLinkStore = CategoryCategoryComboLinkStore.create(databaseAdapter());
        return categoryCategoryComboLinkStore.selectAll();
    }

    private void assertThereAreCategoryOptionCombosInDB() {
        IdentifiableObjectStore<CategoryOptionCombo> categoryOptionComboStore = CategoryOptionComboStoreImpl.create(databaseAdapter());
        List<CategoryOptionCombo> categoryOptionCombos = categoryOptionComboStore.selectAll();
        assertTrue(categoryOptionCombos.size() > 0);
    }

    private void assertThereAreCategoriesInDB() {
        IdentifiableObjectStore<CategoryOption> categoryOptionStore = CategoryOptionStore.create(databaseAdapter());
        List<String> categoryOptionUids = categoryOptionStore.selectUids();
        assertTrue(categoryOptionUids.size() > 0);
    }
}

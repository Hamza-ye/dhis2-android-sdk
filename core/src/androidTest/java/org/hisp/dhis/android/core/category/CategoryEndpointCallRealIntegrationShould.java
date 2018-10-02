package org.hisp.dhis.android.core.category;


import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.junit.Before;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertFalse;

public class CategoryEndpointCallRealIntegrationShould extends AbsStoreTestCase {

    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }

    //@Test
    public void call_categories_endpoint() throws Exception {
        d2.logIn(RealServerMother.user, RealServerMother.password).call();

        CategoryService categoryService = d2.retrofit().create(CategoryService.class);
        Call<List<Category>> categoryEndpointCall = CategoryEndpointCall.factory(categoryService).create(getGenericCallData(d2));
        List<Category> categories = categoryEndpointCall.call();

        assertFalse(categories.isEmpty());
    }
}

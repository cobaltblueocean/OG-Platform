/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.web.analytics.rest;

import static com.opengamma.web.analytics.rest.MarketDataSnapshotListResource.BASIS_VIEW_NAME;
import static com.opengamma.web.analytics.rest.MarketDataSnapshotListResource.ID;
import static com.opengamma.web.analytics.rest.MarketDataSnapshotListResource.NAME;
import static com.opengamma.web.analytics.rest.MarketDataSnapshotListResource.SNAPSHOTS;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jetty.server.Server;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.Test;

import com.opengamma.core.marketdatasnapshot.impl.ManageableMarketDataSnapshot;
import com.opengamma.id.UniqueId;
import com.opengamma.master.marketdatasnapshot.MarketDataSnapshotDocument;
import com.opengamma.master.marketdatasnapshot.MarketDataSnapshotMaster;
import com.opengamma.master.marketdatasnapshot.MarketDataSnapshotSearchRequest;
import com.opengamma.master.marketdatasnapshot.MarketDataSnapshotSearchResult;
import com.opengamma.master.marketdatasnapshot.impl.InMemorySnapshotMaster;
import com.opengamma.util.test.TestGroup;
import com.opengamma.util.tuple.Pair;
import com.opengamma.web.analytics.push.WebPushTestUtils;

/**
 * Test.
 */
@Test(groups = TestGroup.UNIT)
public class MarketDataSnapshotListResourceTest {

  private static MarketDataSnapshotDocument createSnapshot(String basisViewName, String name, UniqueId uid) {
    ManageableMarketDataSnapshot snapshot = new ManageableMarketDataSnapshot();
    snapshot.setBasisViewName(basisViewName);
    snapshot.setName(name);
    snapshot.setUniqueId(uid);
    return new MarketDataSnapshotDocument(snapshot);
  }

  private static MarketDataSnapshotListResource createResource(MarketDataSnapshotDocument... docs) {
    MarketDataSnapshotMaster snapshotMaster = mock(MarketDataSnapshotMaster.class);
    List<MarketDataSnapshotDocument> documents = Arrays.asList(docs);
    MarketDataSnapshotSearchResult result = new MarketDataSnapshotSearchResult(documents);
    when(snapshotMaster.search(any(MarketDataSnapshotSearchRequest.class))).thenReturn(result);
    return new MarketDataSnapshotListResource(snapshotMaster);
  }

  @Test
  public void getJson() throws JSONException {
    MarketDataSnapshotListResource resource = createResource(
        createSnapshot("basisView1", "snap1", UniqueId.of("Tst", "1")),
        createSnapshot("basisView1", "snap2", UniqueId.of("Tst", "2")),
        createSnapshot("basisView2", "snap3", UniqueId.of("Tst", "3")));

    String json = resource.getMarketDataSnapshotList();
    JSONArray jsonArray = new JSONArray(json);

    String expected =
        "[{\"basisViewName\": \"basisView1\", \"snapshots\": [{\"id\": \"Tst~1\", \"name\": \"snap1\"}, {\"id\": \"Tst~2\", \"name\": \"snap2\"}]}, " +
        " {\"basisViewName\": \"basisView2\", \"snapshots\": [{\"id\": \"Tst~3\", \"name\": \"snap3\"}]}]";
    JSONArray expectedArray = new JSONArray(expected);
    assertEquals(expectedArray.toString(), jsonArray.toString());
  }

  @Test
  public void snapshotWithBlankNameNotReturned() throws JSONException {
    MarketDataSnapshotListResource resource = createResource(
        createSnapshot("basisView", "", UniqueId.of("Tst", "2")));

    JSONArray json = new JSONArray(resource.getMarketDataSnapshotList());
    assertEquals(0, json.length());
  }

  @Test
  public void snapshotWithNullIdNotReturned() throws JSONException {
    MarketDataSnapshotListResource resource = createResource(
        createSnapshot("basisView", "snap", null));

    JSONArray json = new JSONArray(resource.getMarketDataSnapshotList());
    assertEquals(0, json.length());
  }

  @Test
  public void snapshotWithAutoGeneratedNameNotReturned() throws JSONException {
    MarketDataSnapshotListResource resource = createResource(
        createSnapshot("basisView", "{12345678-1234-abcd-cdef-0123456789ab}", UniqueId.of("Tst", "1")),
        createSnapshot("basisView", "12345678-1234-abcd-cdef-0123456789ab", UniqueId.of("Tst", "2")));

    JSONArray json = new JSONArray(resource.getMarketDataSnapshotList());
    assertEquals(0, json.length());
  }

  @Test
  public void getJsonOverHttp() throws Exception {
    WebPushTestUtils _webPushTestUtils = new WebPushTestUtils();
    Pair<Server, WebApplicationContext> serverAndContext =
        _webPushTestUtils.createJettyServer("classpath:/com/opengamma/web/analytics/push/marketdatasnapshotlist-test.xml");
    Server server = serverAndContext.getFirst();
    WebApplicationContext context = serverAndContext.getSecond();
    InMemorySnapshotMaster snapshotMaster = context.getBean(InMemorySnapshotMaster.class);
    snapshotMaster.add(createSnapshot("basisView1", "snap1", null));
    JSONArray json = new JSONArray(_webPushTestUtils.readFromPath("/jax/marketdatasnapshots"));
    assertEquals(1, json.length());
    JSONObject basis1Map = json.getJSONObject(0);
    assertEquals("basisView1", basis1Map.get(BASIS_VIEW_NAME));
    JSONArray snapshots = basis1Map.getJSONArray(SNAPSHOTS);
    assertEquals(1, snapshots.length());
    JSONObject snap1 = snapshots.getJSONObject(0);
    assertEquals("snap1", snap1.get(NAME));
    assertEquals(UniqueId.of("MemSnap", "1").toString(), snap1.get(ID));
    server.stop();
  }
}

/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame.marketdata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.opengamma.util.result.FailureStatus;
import com.opengamma.util.result.Result;

/**
 * Market data source that gets data from multiple underlying sources.
 */
public class CompositeMarketDataSource implements MarketDataSource {

  private final List<MarketDataSource> _dataSources;

  /**
   * @param dataSources the underlying data sources that provide the data
   */
  public CompositeMarketDataSource(List<MarketDataSource> dataSources) {
    _dataSources = ImmutableList.copyOf(dataSources);
  }

  @Override
  public Map<MarketDataRequest, Result<?>> get(Set<MarketDataRequest> requests) {
    ImmutableMap.Builder<MarketDataRequest, Result<?>> dataBuilder = ImmutableMap.builder();
    Set<MarketDataRequest> outstandingRequests = requests;

    for (MarketDataSource dataSource : _dataSources) {
      Map<MarketDataRequest, Result<?>> data = dataSource.get(outstandingRequests);
      PartitionedResults results = new PartitionedResults(data);
      dataBuilder.putAll(results._available);
      outstandingRequests = results._unavailable;
    }
    for (MarketDataRequest unavailable : outstandingRequests) {
      // we're not using the failure status from the result because there's no way of knowing whether it's relevant.
      // the failure status that we have is the result from the last source, and there's nothing to say that that's
      // the source where it should have been available. if the data isn't available the user knows it isn't
      // available from any of the sources. the user is also in the best position to know which source should be
      // able to provide the data. this class doesn't have any way of knowing that
      dataBuilder.put(unavailable, Result.failure(FailureStatus.MISSING_DATA, "No data available for {}", unavailable));
    }
    return dataBuilder.build();
  }

  /**
   * Helper class to divide the results into successes and failures
   */
  private static class PartitionedResults {
    
    /** Data that was successfully requested from the data source. */
    private final Map<MarketDataRequest, Result<?>> _available;
    
    /** Requests for that that weren't satisfied. */
    private final Set<MarketDataRequest> _unavailable;

    private PartitionedResults(Map<MarketDataRequest, Result<?>> allData) {
      Set<MarketDataRequest> unavailable = new HashSet<>();
      Map<MarketDataRequest, Result<?>> data = new HashMap<>();
      
      for (Map.Entry<MarketDataRequest, Result<?>> entry : allData.entrySet()) {
        MarketDataRequest request = entry.getKey();
        Result<?> result = entry.getValue();

        if (result.isSuccess()) {
          data.put(request, result);
        } else {
          unavailable.add(request);
        }
      }
      _available = data;
      _unavailable = unavailable;
    }
  }
}

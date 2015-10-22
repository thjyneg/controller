/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.controller.cluster.datastore.jmx.mbeans.shardmanager;

import java.util.List;

public interface ShardManagerInfoMBean {
    /**
     *
     * @return a list of all the local shard names
     */
    List<String> getLocalShards();

    /**
     *
     * @return true if all local shards are in sync with their corresponding leaders
     */
    boolean getSyncStatus();

    /**
     * Get the name of of the current member
     *
     * @return
     */
    String getMemberName();

    /**
     * Switch the Raft Behavior of all the local shards to the newBehavior
     *
     * @param newBehavior should be either Leader/Follower only
     * @param term when switching to the Leader specifies for which term the Shard would be the Leader. Any modifications
     *             made to state will be written with this term. This term will then be used by the Raft replication
     *             implementation to decide which modifications should stay and which ones should be removed. Ideally
     *             the term provided when switching to a new Leader should always be higher than the previous term.
     */
    void switchAllLocalShardsState(String newBehavior, long term);

    /**
     * Switch the Raft Behavior of the shard specified by shardName to the newBehavior
     *
     * @param shardName a shard that is local to this shard manager
     * @param newBehavior should be either Leader/Follower only
     * @param term when switching to the Leader specifies for which term the Shard would be the Leader. Any modifications
     *             made to state will be written with this term. This term will then be used by the Raft replication
     *             implementation to decide which modifications should stay and which ones should be removed. Ideally
     *             the term provided when switching to a new Leader should always be higher than the previous term.
     */
    void switchShardState(String shardName, String newBehavior, long term);

    /**
     * Add a new Shard replica for an existing Shard in this controller node
     *
     * @param shardName the shard that is to be created and replicated in this controller instance
     */
    void setAddShardReplica (String shardName);

    /**
     * Remove a Shard replica available in this controller node
     *
     * @param shardName the shard that is to be removed from this controller instance
     */
    void setRemoveShardReplica (String shardName);

}

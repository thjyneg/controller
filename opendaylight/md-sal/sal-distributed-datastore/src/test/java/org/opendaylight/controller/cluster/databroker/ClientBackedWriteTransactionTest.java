/*
 * Copyright (c) 2017 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.controller.cluster.databroker;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.opendaylight.controller.cluster.databroker.actors.dds.ClientTransaction;
import org.opendaylight.mdsal.dom.spi.store.DOMStoreThreePhaseCommitCohort;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.api.schema.ContainerNode;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class ClientBackedWriteTransactionTest extends ClientBackedTransactionTest<ClientBackedWriteTransaction> {
    private ClientBackedWriteTransaction object;

    @Mock
    private ClientTransaction delegate;
    @Mock
    private ContainerNode data;
    @Mock
    private DOMStoreThreePhaseCommitCohort readyCohort;

    @Before
    public void setUp() {
        doReturn(TRANSACTION_ID).when(delegate).getIdentifier();
        doReturn(readyCohort).when(delegate).ready();

        object = new ClientBackedWriteTransaction(delegate, null);
    }

    @Override
    ClientBackedWriteTransaction object() {
        return object;
    }

    @Test
    public void testWrite() {
        object().write(YangInstanceIdentifier.of(), data);
        verify(delegate).write(YangInstanceIdentifier.of(), data);
    }

    @Test
    public void testMerge() {
        object().merge(YangInstanceIdentifier.of(), data);
        verify(delegate).merge(YangInstanceIdentifier.of(), data);
    }

    @Test
    public void testDelete() {
        object().delete(YangInstanceIdentifier.of());
        verify(delegate).delete(YangInstanceIdentifier.of());
    }

    @Test
    public void testReady() {
        final DOMStoreThreePhaseCommitCohort result = object().ready();
        assertNotNull(result);
        verify(delegate).ready();
    }
}

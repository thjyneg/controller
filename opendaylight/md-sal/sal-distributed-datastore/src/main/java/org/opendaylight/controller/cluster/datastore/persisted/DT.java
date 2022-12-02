/*
 * Copyright (c) 2022 PANTHEON.tech, s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.controller.cluster.datastore.persisted;

import static com.google.common.base.Verify.verifyNotNull;
import static java.util.Objects.requireNonNull;

import com.google.common.io.ByteStreams;
import java.io.IOException;
import org.opendaylight.controller.cluster.access.concepts.ClientIdentifier;
import org.opendaylight.controller.cluster.datastore.persisted.AbstractIdentifiablePayload.SerialForm;

/**
 * Serialization proxy for {@link DisableTrackingPayload}.
 */
final class DT implements SerialForm {
    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private ClientIdentifier identifier;
    private byte[] bytes;

    @SuppressWarnings("checkstyle:RedundantModifier")
    public DT() {
        // For Externalizable
    }

    DT(final byte[] bytes) {
        this.bytes = requireNonNull(bytes);
    }

    @Override
    public byte[] bytes() {
        return bytes;
    }

    @Override
    public void readExternal(final byte[] newBytes) throws IOException {
        bytes = requireNonNull(newBytes);
        identifier = verifyNotNull(ClientIdentifier.readFrom(ByteStreams.newDataInput(newBytes)));
    }

    @Override
    public Object readResolve() {
        return new DisableTrackingPayload(identifier, bytes);
    }
}

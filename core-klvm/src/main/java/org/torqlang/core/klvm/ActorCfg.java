/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.torqlang.core.util.NeedsImpl;

import java.util.List;

public final class ActorCfg implements Obj {

    private final List<Complete> args;
    private final Closure handlerCtor;

    public ActorCfg(List<Complete> args, Closure handlerCtor) {
        this.args = args;
        this.handlerCtor = handlerCtor;
    }

    @Override
    public final <T, R> R accept(KernelVisitor<T, R> visitor, T state) throws Exception {
        return visitor.visitActorCfg(this, state);
    }

    public final List<Complete> args() {
        return args;
    }

    public final Closure handlerCtor() {
        return handlerCtor;
    }

    public final ValueOrVar select(Feature feature) throws WaitException {
        throw new NeedsImpl();
    }

    @Override
    public final String toString() {
        return toKernelString();
    }

}

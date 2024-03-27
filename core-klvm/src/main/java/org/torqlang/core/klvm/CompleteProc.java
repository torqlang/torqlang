/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

/*
 * When CompleteProc is used as a functional interface, the function is inheriting:
 *     - entails from Value, which compares on identity using `==`
 *     - equals from Object, which compares on identity using `==`
 *     - hashCode from Object, which is an native method returning identity
 *     - isValidKey from CompleteProc, which returns true
 */
public interface CompleteProc extends Complete, Proc {
    @Override
    default boolean isValidKey() {
        return true;
    }
}

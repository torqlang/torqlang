/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.util;

final class SourceLocation {

    public final int lineIndex;
    public final int charIndexInLine;

    public SourceLocation(int lineIndex, int charIndexInLine) {
        this.lineIndex = lineIndex;
        this.charIndexInLine = charIndexInLine;
    }

}

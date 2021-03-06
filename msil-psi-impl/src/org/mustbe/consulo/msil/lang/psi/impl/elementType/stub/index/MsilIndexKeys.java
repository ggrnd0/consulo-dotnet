/*
 * Copyright 2013-2014 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.index;

import org.mustbe.consulo.msil.lang.psi.MsilClassEntry;
import com.intellij.psi.stubs.StubIndexKey;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public interface MsilIndexKeys
{
	StubIndexKey<String, MsilClassEntry> TYPE_BY_QNAME_INDEX = StubIndexKey.createIndexKey("msil.type.by.qname.index");

	StubIndexKey<String, MsilClassEntry> NAMESPACE_INDEX = StubIndexKey.createIndexKey("msil.namespace.index");

	StubIndexKey<String, MsilClassEntry> ALL_NAMESPACE_INDEX = StubIndexKey.createIndexKey("msil.all.namespace.index");
}

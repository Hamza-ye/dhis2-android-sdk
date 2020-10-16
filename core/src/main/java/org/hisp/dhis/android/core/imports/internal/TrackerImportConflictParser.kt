/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.imports.internal

import dagger.Reusable
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.imports.TrackerImportConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.BadAttributePatternConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.InvalidAttributeValueTypeConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.InvalidDataValueConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.MissingAttributeConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.MissingDataElementConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.NonUniqueAttributeConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.TrackerImportConflictItem
import org.hisp.dhis.android.core.imports.internal.conflicts.TrackerImportConflictItemContext
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueCollectionRepository
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueCollectionRepository
import javax.inject.Inject

@Reusable
internal class TrackerImportConflictParser @Inject constructor(
        attributeStore: IdentifiableObjectStore<TrackedEntityAttribute>,
        dataElementStore: IdentifiableObjectStore<DataElement>,
        private val trackedEntityAttributeValueRepository: TrackedEntityAttributeValueCollectionRepository,
        private val trackedEntityInstanceDataValueRepository: TrackedEntityDataValueCollectionRepository
) {

    private val context = TrackerImportConflictItemContext(attributeStore, dataElementStore)

    private val trackedEntityInstanceConflicts: List<TrackerImportConflictItem> = listOf(
            InvalidAttributeValueTypeConflict,
            MissingAttributeConflict,
            BadAttributePatternConflict,
            NonUniqueAttributeConflict
    )

    private val enrollmentConflicts: List<TrackerImportConflictItem> = listOf(
            InvalidAttributeValueTypeConflict,
            MissingAttributeConflict,
            BadAttributePatternConflict,
            NonUniqueAttributeConflict
    )

    private val eventConflicts: List<TrackerImportConflictItem> = listOf(
            InvalidDataValueConflict,
            MissingDataElementConflict
    )

    fun getTrackedEntityInstanceConflict(conflict: ImportConflict,
                                         conflictBuilder: TrackerImportConflict.Builder): TrackerImportConflict {
        return evaluateConflicts(conflict, conflictBuilder, trackedEntityInstanceConflicts)
    }

    fun getEnrollmentConflict(conflict: ImportConflict,
                              conflictBuilder: TrackerImportConflict.Builder): TrackerImportConflict {
        return evaluateConflicts(conflict, conflictBuilder, enrollmentConflicts)
    }

    fun getEventConflict(conflict: ImportConflict,
                         conflictBuilder: TrackerImportConflict.Builder): TrackerImportConflict {
        return evaluateConflicts(conflict, conflictBuilder, eventConflicts)
    }

    private fun evaluateConflicts(conflict: ImportConflict,
                                  conflictBuilder: TrackerImportConflict.Builder,
                                  conflictTypes: List<TrackerImportConflictItem>): TrackerImportConflict {
        val conflictType = conflictTypes.find { it.matches(conflict) }

        if (conflictType != null) {
            conflictBuilder
                    .errorCode(conflictType.errorCode)
                    .displayDescription(conflictType.getDisplayDescription(conflict, context))
                    .trackedEntityAttribute(conflictType.getTrackedEntityAttribute(conflict))
                    .dataElement(conflictType.getDataElement(conflict))
        } else {
            conflictBuilder
                    .displayDescription(conflict.value())
        }

        return conflictBuilder
                .conflict(conflict.value())
                .value(getConflictValue(conflictBuilder))
                .build()
    }

    private fun getConflictValue(conflictBuilder: TrackerImportConflict.Builder): String? {
        val auxConflict = conflictBuilder.build()

        return if (auxConflict.dataElement() != null && auxConflict.event() != null) {
            trackedEntityInstanceDataValueRepository
                    .value(auxConflict.event(), auxConflict.dataElement())
                    .blockingGet()?.value()
        } else if (auxConflict.trackedEntityAttribute() != null && auxConflict.trackedEntityInstance() != null) {
            trackedEntityAttributeValueRepository
                    .value(auxConflict.trackedEntityAttribute(), auxConflict.trackedEntityInstance())
                    .blockingGet()?.value()
        } else {
            null
        }
    }
}

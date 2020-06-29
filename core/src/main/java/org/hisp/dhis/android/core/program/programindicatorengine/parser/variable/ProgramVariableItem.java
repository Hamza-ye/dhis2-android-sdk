package org.hisp.dhis.android.core.program.programindicatorengine.parser.variable;

/*
 * Copyright (c) 2004-2020, University of Oslo
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

import com.google.common.collect.ImmutableMap;

import org.hisp.dhis.android.core.parser.expression.CommonExpressionVisitor;
import org.hisp.dhis.android.core.parser.expression.ExpressionItem;
import org.hisp.dhis.android.core.program.programindicatorengine.parser.ProgramExpressionItem;
import org.hisp.dhis.antlr.ParserExceptionWithoutContext;

import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.ExprContext;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.V_CURRENT_DATE;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.V_DUE_DATE;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.V_ENROLLMENT_COUNT;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.V_ENROLLMENT_DATE;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.V_ENROLLMENT_STATUS;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.V_EVENT_COUNT;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.V_EVENT_DATE;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.V_INCIDENT_DATE;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.V_VALUE_COUNT;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.V_ZERO_POS_VALUE_COUNT;

public class ProgramVariableItem
        extends ProgramExpressionItem {

    private final static ImmutableMap<Integer, ExpressionItem> PROGRAM_VARIABLES = ImmutableMap.<Integer, ExpressionItem>builder()

            .put(V_ENROLLMENT_DATE, new vEnrollmentDate())
            .put(V_INCIDENT_DATE, new vIncidentDate())
            .put(V_EVENT_DATE, new vEventDate())
            .put(V_DUE_DATE, new vDueDate())
            .put(V_CURRENT_DATE, new vCurrentDate())

            .put(V_ENROLLMENT_STATUS, new vEnrollmentStatus())
            .put(V_ENROLLMENT_COUNT, new vEnrollmentCount())

            .put(V_EVENT_COUNT, new vEventCount())
            .put(V_VALUE_COUNT, new vValueCount())
            .put(V_ZERO_POS_VALUE_COUNT, new vZeroPosValueCount())
            .build();

    @Override
    public Object evaluate(ExprContext ctx, CommonExpressionVisitor visitor) {
        ExpressionItem programVariable = getProgramVariable(ctx);

        return programVariable.evaluate(ctx, visitor);
    }

    @Override
    public Object count(ExprContext ctx, CommonExpressionVisitor visitor) {
        ExpressionItem programVariable = getProgramVariable(ctx);

        return programVariable.count(ctx, visitor);
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private ExpressionItem getProgramVariable(ExprContext ctx) {
        ExpressionItem programVariable = PROGRAM_VARIABLES.get(ctx.programVariable().var.getType());

        if (programVariable == null) {
            throw new ParserExceptionWithoutContext("Can't find program variable " + ctx.programVariable().var.getText());
        }

        return programVariable;
    }
}

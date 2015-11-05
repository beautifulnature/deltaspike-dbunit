/*
 * Copyright 2002-2015 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.deltaspikedbunit.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.deltaspikedbunit.assertion.DatabaseAssertionMode;
import com.github.deltaspikedbunit.dataset.DataSetModifier;
import org.dbunit.dataset.IDataSet;


/**
 * Asserts that a database is in given state after tests have run.
 *
 * @author Luigi Bitonti
 * @author Phillip Webb
 * @author Mario Zagar
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface ExpectedDatabase {

	/**
	 * Provides the location of the dataset that will be used to test the database.
	 * Locations are relative to the class under test.
     *
	 * @return The dataset location
	 * @see DbUnitConfiguration#dataSetLoader()
	 */
    String value() default "";


	/**
	 * Database assertion mode to use. Default is {@link DatabaseAssertionMode#DEFAULT}.
     *
	 * @return Database assertion mode to use
	 */
	DatabaseAssertionMode assertionMode() default DatabaseAssertionMode.DEFAULT;

	/**
	 * Optional table name that can be used to limit the comparison to a specific table.
     *
	 * @return the table name
	 */
	String table() default "";

	/**
	 * Optional SQL to retrieve the actual subset of the table rows from the database. NOTE: a {@link #table() table
	 * name} must also be specified when using a query.
     *
	 * @return the SQL Query
	 */
	String query() default "";

	/**
	 * If this expectation overrides any others that have been defined at a higher level. Defaults to {@code true}
	 * @return if this annotation overrides any others
	 */
	boolean override() default true;

	/**
	 * A set of {@link DataSetModifier} that will be applied to the {@link IDataSet} before it is used. Can refer to a
	 * static or inner class of the test.
     *
	 * @return the modifiers to apply
	 */
	Class<? extends DataSetModifier>[] modifiers() default {};

}
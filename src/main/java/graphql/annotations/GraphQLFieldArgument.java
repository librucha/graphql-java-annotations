/**
 * Copyright 2016 Yurii Rashkovskii
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */
package graphql.annotations;

import java.lang.annotation.*;
import java.util.function.Supplier;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(GraphQLFieldArgument.GraphQLFieldArguments.class)
public @interface GraphQLFieldArgument {

    String name();

    Class<?> type();

    String description() default "";

    Class<? extends Supplier<Object>> defaultValue() default NullDefaultValue.class;

    class NullDefaultValue implements Supplier<Object> {
        @Override
        public Object get() {
            return null;
        }
    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface GraphQLFieldArguments {
        GraphQLFieldArgument[] value();
    }
}

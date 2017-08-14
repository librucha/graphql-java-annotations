/**
 * Copyright 2016 Yurii Rashkovskii
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */
package graphql.annotations;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.Scalars;
import graphql.schema.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.testng.Assert.*;

@SuppressWarnings("unchecked")
public class GraphQLFieldArgumentTest {

    private GraphQL graphQL;

    public static class DefaultValue implements Supplier<Object> {

        @Override
        public Object get() {
            return "default";
        }
    }

    public static class InnerTestObjectDataFetcher implements DataFetcher<InnerTestObject> {

        @Override
        public InnerTestObject get(DataFetchingEnvironment environment) {
            Long id = environment.getArgument("id");
            String name = environment.getArgument("name");
            InnerTestObject result = new InnerTestObject();
            if (id != null) {
                result.setName(id.toString());
            } else if (name != null) {
                result.setName(name);
            }
            return result;
        }
    }

    private static class InnerTestObject {
        @GraphQLField
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @GraphQLDescription("TestObject object")
    @GraphQLName("TestObject")
    private static class TestObject {
        @GraphQLField
        @GraphQLDataFetcher(value = InnerTestObjectDataFetcher.class)
        @GraphQLFieldArgument(name = "id", type = Long.class, defaultValue = DefaultValue.class, description = "Named argument on field")
        @GraphQLFieldArgument(name = "name", type = String.class, defaultValue = DefaultValue.class, description = "Named argument on field")
        private InnerTestObject innerTestObject;
    }

    @BeforeMethod
    public void setUp() throws Exception {
        GraphQLObjectType object = GraphQLAnnotations.object(TestObject.class);
        GraphQLSchema schema = GraphQLSchema.newSchema()
                .query(object)
                .build();
        graphQL = GraphQL.newGraphQL(schema).build();
    }

    @Test
    public void fieldWithArgument() {
        GraphQLObjectType object = GraphQLAnnotations.object(TestObject.class);
        List<GraphQLFieldDefinition> fields = object.getFieldDefinitions();
        assertEquals(fields.size(), 1);

        List<GraphQLArgument> args = fields.get(0).getArguments();
        assertEquals(args.size(), 2);

        GraphQLArgument arg0 = args.get(0);
        assertEquals(arg0.getName(), "id");
        assertEquals(arg0.getDescription(), "Named argument on field");
        assertEquals(arg0.getType(), Scalars.GraphQLLong);
//        assertEquals(arg0.getDefaultValue(), "default");

        GraphQLArgument arg1 = args.get(1);
        assertEquals(arg1.getName(), "name");
        assertEquals(arg1.getDescription(), "Named argument on field");
        assertEquals(arg1.getType(), Scalars.GraphQLString);
//        assertEquals(arg1.getDefaultValue(), "default");
    }

    @Test
    public void fetchDataById() throws Exception {
        ExecutionResult result = graphQL.execute("{innerTestObject(id: 10){name}}");
        Map<String, Object> data = result.getData();

        assertNotNull(data);
        assertTrue(result.getErrors().isEmpty());
        assertEquals(data.get("innerTestObject").toString(), "{name=10}");
    }

    @Test
    public void fetchDataByName() throws Exception {
        ExecutionResult result = graphQL.execute("{innerTestObject(name: \"TestName\"){name}}");
        Map<String, Object> data = result.getData();

        assertNotNull(data);
        assertTrue(result.getErrors().isEmpty());
        assertEquals(data.get("innerTestObject").toString(), "{name=TestName}");
    }
}

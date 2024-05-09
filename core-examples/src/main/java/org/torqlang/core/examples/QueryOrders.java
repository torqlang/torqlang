/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.examples;

import org.torqlang.core.klvm.Complete;
import org.torqlang.core.klvm.CompleteRec;
import org.torqlang.core.klvm.Rec;
import org.torqlang.core.klvm.Str;
import org.torqlang.core.lang.ValueTools;
import org.torqlang.core.local.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class QueryOrders extends AbstractExample {

    public static final String SOURCE = """
        // meta#{'path': '/orders?{query}'}
        actor Orders() in
            import system[ArrayList, FieldIter, ValueIter]
            import examples.NorthwindCache.get_orders
            var orders = get_orders()
            handle ask 'GET'#{'headers': headers, 'query': query} in
                func matches_query(order) in
                    for field in FieldIter.new(query) do
                        if order[field.0] != field.1 then
                            return false
                        end
                    end
                    true
                end
                var array_list = ArrayList.new()
                for order in ValueIter.new(orders) do
                    if matches_query(order) then
                        array_list.add(order);
                    end
                end
                array_list.to_tuple()
            end
        end""";

    public static void main(String[] args) throws Exception {
        new QueryOrders().performWithErrorCheck();
        System.exit(0);
    }

    @Override
    public final void perform() throws Exception {

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.findStatic(NorthwindCache.class, "getOrders", MethodType.methodType(Complete.class));
        CompleteRec moduleRec = Rec.completeRecBuilder()
            .addField(Str.of("get_orders"), new AsyncMethod(methodHandle))
            .build();

        ActorSystem system = ActorSystem.builder()
            .addDefaultModules()
            .addModule("examples.NorthwindCache", moduleRec)
            .build();

        ApiRouter router = ApiRouter.staticBuilder()
            .addRoute("/orders", Actor.builder()
                .setSystem(system)
                .spawn(SOURCE)
                .actorRef())
            .build();
        Map<?, ?> requestMap = Map.of(
            "$label", "GET",
            "$rec", Map.of(
                "headers", Map.of(),
                "query", Map.of(
                    "ship_city", "Las Vegas"
                )
            )
        );

        ApiRoute route = router.findRoute(new ApiPath("/orders"));

        Object response = RequestClient.builder().sendAndAwaitResponse(
            route.actorRef,
            ValueTools.toKernelValue(requestMap),
            Long.MAX_VALUE, TimeUnit.MILLISECONDS);

        checkNotFailedValue(response);
        List<?> nativeResponse = (List<?>) ValueTools.toNativeValue((Complete) response);
        checkExpectedResponse(4, nativeResponse.size());
        for (Object obj : nativeResponse) {
            checkExpectedResponse("Las Vegas", ((Map<?,?>) obj).get("ship_city"));
        }
    }

}
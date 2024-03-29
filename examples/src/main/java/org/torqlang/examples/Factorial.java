/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.examples;

import org.torqlang.core.actor.ActorRef;
import org.torqlang.core.klvm.Dec128;
import org.torqlang.core.klvm.FailedValue;
import org.torqlang.core.local.RequestClient;

import java.util.concurrent.TimeUnit;

import static org.torqlang.core.local.ActorSystem.actorBuilder;
import static org.torqlang.core.local.ActorSystem.createAddress;

public class Factorial {

    public static final String SOURCE = """
        actor Factorial() in
            func fact(x) in
                func fact_cps(n, k) in
                    if n < 2m then k
                    else fact_cps(n - 1m, n * k) end
                end
                fact_cps(x, 1m)
            end
            ask x in
                fact(x)
            end
        end""";

    public static void main(String[] args) throws Exception {
        perform();
        System.exit(0);
    }

    public static void perform() throws Exception {
        ActorRef actorRef = actorBuilder()
            .setAddress(createAddress(Factorial.class.getName()))
            .setSource(SOURCE)
            .spawn();
        Object response = RequestClient.builder()
            .setAddress(createAddress("FactorialClient"))
            .send(actorRef, Dec128.of(10))
            .awaitResponse(100, TimeUnit.MILLISECONDS);
        if (!response.equals(Dec128.of(3628800))) {
            String error = "Invalid response: " + response;
            if (response instanceof FailedValue failedValue) {
                error += "\n" + failedValue.toDetailsString();
            }
            throw new IllegalStateException(error);
        }
    }

}

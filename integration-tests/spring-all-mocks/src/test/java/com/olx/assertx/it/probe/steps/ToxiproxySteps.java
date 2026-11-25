package com.olx.assertx.it.probe.steps;

import com.olx.assertx.stepdefinitions.BaseStepDefinition;
import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.model.ToxicDirection;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/** Exercises BaseStepDefinition#getToxiProxyClient() against the proxies AssertX generates per mock. */
public class ToxiproxySteps extends BaseStepDefinition {

    private static final Set<String> TOUCHED = new HashSet<>();

    private Proxy proxy(String name) throws IOException {
        TOUCHED.add(name);
        return getToxiProxyClient().getProxy(name);
    }

    @Given("the {string} proxy adds {int} ms of latency")
    public void addLatency(String name, int millis) throws IOException {
        proxy(name).toxics().latency("regression-latency", ToxicDirection.DOWNSTREAM, millis);
    }

    @Given("the {string} proxy is disabled")
    public void disable(String name) throws IOException {
        proxy(name).disable();
    }

    @When("the {string} proxy is restored")
    public void restore(String name) throws IOException {
        Proxy proxy = proxy(name);
        for (var toxic : proxy.toxics().getAll()) {
            toxic.remove();
        }
        proxy.enable();
    }

    /** Never leak chaos into the next scenario, even when one fails mid-way. */
    @After
    public void restoreTouchedProxies() throws IOException {
        for (String name : new HashSet<>(TOUCHED)) {
            restore(name);
        }
        TOUCHED.clear();
    }
}

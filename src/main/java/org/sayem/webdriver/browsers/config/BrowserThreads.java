package org.sayem.webdriver.browsers.config;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.sayem.webdriver.TestBase;
import org.sayem.webdriver.properties.Repository;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;

import static org.openqa.selenium.Proxy.ProxyType.MANUAL;
import static org.sayem.webdriver.browsers.config.BrowserType.FIREFOX;
import static org.sayem.webdriver.browsers.config.BrowserType.valueOf;

/**
 * Created by sayem on 10/05/15.
 */
public class BrowserThreads {

    private final String defaultUrl = System.getProperty("seleniumUrl");
    private final BrowserType defaultBrowserType = getBrowser();
    private final String browser = System.getProperty("browser", defaultBrowserType.toString()).toUpperCase();
    private final String operatingSystem = System.getProperty("os.name").toUpperCase();
    private final String systemArchitecture = System.getProperty("os.arch");
    private final boolean useRemoteWebDriver = Boolean.getBoolean("remoteDriver");
    private final boolean proxyEnabled = Boolean.getBoolean("proxyEnabled");
    private final String proxyHostname = System.getProperty("proxyHost");
    private final Integer proxyPort = Integer.getInteger("proxyPort");
    private final String proxyDetails = String.format("%s:%d", proxyHostname, proxyPort);
    private WebDriver webdriver;
    private BrowserType selectedDriverType;
    private BrowserMobProxy browserMobProxy;
    private boolean usingBrowserMobProxy = false;

    private WebDriver getDriver(boolean useBrowserMobProxy) {
        if (null != webdriver && usingBrowserMobProxy != useBrowserMobProxy) {
            webdriver.quit();
            webdriver = null;
        }
        if (null == webdriver) {
            Proxy proxy = null;
            if (proxyEnabled || useBrowserMobProxy) {
                if (useBrowserMobProxy) {
                    usingBrowserMobProxy = true;
                    browserMobProxy = new BrowserMobProxyServer();
                    browserMobProxy.start();
                    if (proxyEnabled) {
                        browserMobProxy.setChainedProxy(new InetSocketAddress(proxyHostname, proxyPort));
                    }
                    proxy = ClientUtil.createSeleniumProxy(browserMobProxy);
                } else {
                    proxy = new Proxy();
                    proxy.setProxyType(MANUAL);
                    proxy.setHttpProxy(proxyDetails);
                    proxy.setSslProxy(proxyDetails);
                }
            }
            determineEffectiveDriverType();
            DesiredCapabilities desiredCapabilities = selectedDriverType.browser.getDesiredCapabilities(proxy);
            instantiateWebDriver(desiredCapabilities);
        }

        return webdriver;
    }

    public WebDriver getDriver() {
        return getDriver(usingBrowserMobProxy);
    }

    public WebDriver getBrowserMobProxyEnabledDriver() {
        return getDriver(true);
    }

    public BrowserMobProxy getBrowserMobProxy() {
        if (usingBrowserMobProxy) {
            return browserMobProxy;
        }
        return null;
    }

    public void quitDriver() {
        if (null != webdriver) {
            webdriver.quit();
        }
    }

    private void determineEffectiveDriverType() {
        BrowserType driverType = defaultBrowserType;
        try {
            driverType = valueOf(browser);
        } catch (IllegalArgumentException ignored) {
            System.err.println("Unknown driver specified, defaulting to '" + driverType + "'...");
        } catch (NullPointerException ignored) {
            System.err.println("No driver specified, defaulting to '" + driverType + "'...");
        }
        selectedDriverType = driverType;
    }

    private void instantiateWebDriver(DesiredCapabilities desiredCapabilities) {
        System.out.println(" ");
        System.out.println("Current Operating System: " + operatingSystem);
        System.out.println("Current Architecture: " + systemArchitecture);
        System.out.println("Current Browser Selection: " + selectedDriverType);
        System.out.println(" ");

        if (useRemoteWebDriver) {
            URL seleniumGridURL = null;
            try {
                seleniumGridURL = new URL(System.getProperty("gridURL"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            String desiredBrowserVersion = System.getProperty("desiredBrowserVersion");
            String desiredPlatform = System.getProperty("desiredPlatform");

            if (null != desiredPlatform && !desiredPlatform.isEmpty()) {
                desiredCapabilities.setPlatform(Platform.valueOf(desiredPlatform.toUpperCase()));
            }

            if (null != desiredBrowserVersion && !desiredBrowserVersion.isEmpty()) {
                desiredCapabilities.setVersion(desiredBrowserVersion);
            }

            webdriver = new RemoteWebDriver(seleniumGridURL, desiredCapabilities);
        } else {
            webdriver = selectedDriverType.browser.getWebDriverObject(desiredCapabilities);
        }
        webdriver.manage().window().setSize(new Dimension(1280, 1024));
        setup();
    }

    private void setup() {
        if (defaultUrl.isEmpty()) {
            System.err.println("No URL specified, defaulting to: " + defaultUrl + "'...");
            webdriver.navigate().to(TestBase.getProperties(Repository.URL));
        }else {
            webdriver.navigate().to(defaultUrl);
        }
    }

    private BrowserType getBrowser() {
        BrowserType browserType = FIREFOX;
        try {
            browserType = valueOf(TestBase.getProperties(Repository.BROWSER).toUpperCase());
        } catch (IllegalArgumentException ignored) {
            System.err.println("Unknown driver specified, defaulting to '" + browserType + "'...");
        } catch (NullPointerException ignored) {
            System.err.println("No driver specified in property, defaulting to '" + browserType + "'...");
        }
        return browserType;
    }
}
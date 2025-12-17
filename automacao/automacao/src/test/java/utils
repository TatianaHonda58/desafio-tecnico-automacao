Código:

@Test
@DisplayName("Dashboard deve carregar em até 5 segundos")
void shouldLoadDashboardWithin5Seconds() {
    long start = System.currentTimeMillis();

    loginPage.login("test_user", "senha123");

    // WebDriverWait com timeout de 5s
    new WebDriverWait(driver, Duration.ofSeconds(5))
        .until(ExpectedConditions.urlContains("/dashboard"));

    new WebDriverWait(driver, Duration.ofSeconds(5))
        .until(ExpectedConditions.visibilityOfElementLocated(By.id("welcome")));

    long end = System.currentTimeMillis();
    long loadTime = end - start;

    assertTrue(loadTime <= 5000, "Dashboard demorou mais de 5s para carregar: " + loadTime + "ms");
}

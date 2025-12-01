package com.opencart.test;

import com.opencart.pages.LoginPage;
import com.opencart.utils.ExcelUtil;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.util.Iterator;
import java.util.List;

public class LoginTest extends BaseTest {

    private String baseUrl = "http://opencart.abstracta.us";
    private String rutaLogs = "src/main/resources/Logs.xlsx";
    private String hojaLogs = "Login";

    @DataProvider(name = "Login")
    public Iterator<Object[]> loginDataProvider() throws Exception {

        String rutaExcel = "src/main/resources/inputData.xlsx";
        String hoja = "DataLogin";

        List<String[]> datos = ExcelUtil.leerDatos(rutaExcel, hoja);

        int startRow = 1; 
        java.util.List<Object[]> lista = new java.util.ArrayList<>();

        for (int i = startRow; i < datos.size(); i++) {
            String[] fila = datos.get(i);

           
            if (fila == null || fila.length < 3) {
                continue;
            }

            boolean todasVacias = true;
            for (int c = 0; c < 3; c++) {
                if (fila[c] != null && !fila[c].trim().isEmpty()) {
                    todasVacias = false;
                    break;
                }
            }
            if (todasVacias) {
                continue;
            }

            Object[] rowData = new Object[]{
                    fila[0], // Email
                    fila[1], // Password
                    fila[2]  // ExpectedResult: SUCCESS / FAIL
            };
            lista.add(rowData);
        }

        return lista.iterator();
    }

    @Test(dataProvider = "Login")
    public void loginTest(String email,
                          String password,
                          String expectedResult) throws Exception {

        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(baseUrl);

        loginPage.setEmail(email);
        loginPage.setPassword(password);
        loginPage.clickLogin();

        boolean warning = loginPage.isWarningDisplayed();

        String estado;
        String mensaje;

        if ("SUCCESS".equalsIgnoreCase(expectedResult)) {
            Assert.assertFalse(warning,
                    "Se esperaba login exitoso, pero apareció un mensaje de error en el login.");
            estado = "EXITOSO";
            mensaje = "Login exitoso";
        } else {
            Assert.assertTrue(warning,
                    "Se esperaba error de login, pero no se mostró el mensaje de advertencia.");
            estado = "FALLIDO";
            mensaje = loginPage.getWarningText();
        }

        
        String[] filaLog = new String[]{
                email,
                password,
                expectedResult,
                estado,
                mensaje
        };

        ExcelUtil.escribirLog(rutaLogs, hojaLogs, filaLog);
    }
}

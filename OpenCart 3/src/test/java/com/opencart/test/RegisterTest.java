package com.opencart.test;

import com.opencart.pages.RegisterPage;
import com.opencart.utils.ExcelUtil;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.util.Iterator;
import java.util.List;

public class RegisterTest extends BaseTest {

    private String baseUrl = "http://opencart.abstracta.us";
    private String rutaLogs = "src/main/resources/Logs.xlsx";
    private String hojaLogs = "Registro";


    @DataProvider(name = "registroUsuarios")
    public Iterator<Object[]> registroUsuariosData() throws Exception {

        String rutaExcel = "src/main/resources/inputData.xlsx";
        String hoja = "RegistroUsuarios";

        List<String[]> datos = ExcelUtil.leerDatos(rutaExcel, hoja);

        int startRow = 1;
        java.util.List<Object[]> lista = new java.util.ArrayList<>();

        for (int i = startRow; i < datos.size(); i++) {
            String[] fila = datos.get(i);

            if (fila == null || fila.length < 6) {
                continue;
            }

            boolean todasVacias = true;
            for (int c = 0; c < 6; c++) {
                if (fila[c] != null && !fila[c].trim().isEmpty()) {
                    todasVacias = false;
                    break;
                }
            }
            if (todasVacias) {
                continue;
            }

            Object[] rowData = new Object[]{
                    fila[0], // FirstName
                    fila[1], // LastName
                    fila[2], // Email
                    fila[3], // Telephone
                    fila[4],  // Password
                    fila[5]  // PasswordConfirm
            };
            lista.add(rowData);
        }

        return lista.iterator();
    }

    @Test(dataProvider = "registroUsuarios")
    public void registroUsuarioExitoso(String firstName,
                                    String lastName,
                                    String email,
                                    String telephone,
                                    String password,
                                    String passwordConfirm) throws Exception {

        RegisterPage registerPage = new RegisterPage(driver);
        registerPage.open(baseUrl);

        registerPage.setFirstName(firstName);
        registerPage.setLastName(lastName);
        registerPage.setEmail(email);
        registerPage.setTelephone(telephone);
        registerPage.setPassword(password);
        registerPage.setConfirmPassword(passwordConfirm);
        registerPage.acceptPrivacyPolicy();
        registerPage.submitForm();

        String mensaje;
        String estado;

        if (registerPage.isErrorAlertDisplayed()) {
            mensaje = registerPage.getErrorAlertText();

            // Si el correo ya estaba registrado, no consideramos que el test falle
            if (mensaje.toLowerCase().contains("already registered")) {
                estado = "YA_REGISTRADO";
            } else {
                estado = "FALLIDO";
            }
        } else {
            mensaje = registerPage.getSuccessMessage();
            Assert.assertTrue(
                    mensaje.toLowerCase().contains("account"),
                    "El mensaje de éxito no es el esperado. Mensaje real: " + mensaje
            );
            estado = "EXITOSO";
        }


        String[] filaLog = new String[]{
                firstName,
                lastName,
                email,
                telephone,
                password,
                passwordConfirm,
                estado,
                mensaje
        };

        ExcelUtil.escribirLog(rutaLogs, hojaLogs, filaLog);

        if ("FALLIDO".equals(estado)) {
            Assert.fail("Registro falló. Mensaje de error: " + mensaje);
        }
    }
}

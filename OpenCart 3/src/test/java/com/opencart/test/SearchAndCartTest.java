package com.opencart.test;

import com.opencart.pages.ProductPage;
import com.opencart.utils.ExcelUtil;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.List;

public class SearchAndCartTest extends BaseTest {

    private String baseUrl = "http://opencart.abstracta.us";
    private String rutaLogs = "src/main/resources/Logs.xlsx";
    private String hojaLogs = "carrito";

    @Test
    public void agregarTodosLosProductosAlCarrito() throws Exception {

        String rutaExcel = "src/main/resources/inputData.xlsx";
        String hoja = "ProductosBusqueda";

        List<String[]> datos = ExcelUtil.leerDatos(rutaExcel, hoja);

        int startRow = 1; 

        driver.get(baseUrl);

        for (int i = startRow; i < datos.size(); i++) {
            String[] fila = datos.get(i);

            if (fila == null || fila.length < 4) {
                continue;
            }

            boolean todasVacias = true;
            for (int c = 0; c < 4; c++) {
                if (fila[c] != null && !fila[c].trim().isEmpty()) {
                    todasVacias = false;
                    break;
                }
            }
            if (todasVacias) {
                continue;
            }

            String categoria = fila[0];
            String subCategoria = fila[1];
            String producto = fila[2];
            String cantidad = fila[3];

            // Buscar producto desde la Home
            homePage.searchProduct(producto);
            homePage.openProductFromResults(producto);

            ProductPage productPage = new ProductPage(driver);

            productPage.selectFirstOptionIfAvailable();
            productPage.setQuantity(cantidad);
            productPage.addToCart();

            // Comprobamos el mensaje de éxito; si no aparece, reintentamos una vez
            boolean exito = productPage.isSuccessMessageDisplayed();
            if (!exito) {
                productPage.addToCart();
                exito = productPage.isSuccessMessageDisplayed();
            }

            String mensaje = exito ? productPage.getSuccessMessageText() : "No se mostró mensaje de éxito";

            Assert.assertTrue(exito,
                    "No se mostró mensaje de éxito al agregar al carrito el producto: " + producto);

            
            String[] filaLog = new String[]{
                    categoria,
                    subCategoria,
                    producto,
                    cantidad,
                    exito ? "AGREGADO" : "ERROR",
                    mensaje
            };

            ExcelUtil.escribirLog(rutaLogs, hojaLogs, filaLog);
        }
    }
}

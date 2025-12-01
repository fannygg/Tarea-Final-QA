package com.opencart.test;

import com.opencart.pages.CartPage;
import com.opencart.utils.ExcelUtil;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartVerificationTest extends BaseTest {

    private String baseUrl = "http://opencart.abstracta.us";
    private String rutaLogs = "src/main/resources/Logs.xlsx";
    private String hojaLogs = "VerificacionCarrito";

    @Test
    public void verificarProductosEnCarrito() throws Exception {

        // Leer productos desde ProductosBusqueda
        String rutaExcel = "src/main/resources/inputData.xlsx";
        String hoja = "ProductosBusqueda";

        List<String[]> datos = ExcelUtil.leerDatos(rutaExcel, hoja);
        int startRow = 1; 

        // Agregar productos al carrito en esta misma sesión
        driver.get(baseUrl);

        for (int i = startRow; i < datos.size(); i++) {
            String[] fila = datos.get(i);
            if (fila == null || fila.length < 4) continue;

            boolean todasVacias = true;
            for (int c = 0; c < 4; c++) {
                if (fila[c] != null && !fila[c].trim().isEmpty()) {
                    todasVacias = false;
                    break;
                }
            }
            if (todasVacias) continue;

            String producto = fila[2].trim();
            String cantidadStr = fila[3].trim();

            // Buscar y agregar al carrito
            homePage.searchProduct(producto);
            homePage.openProductFromResults(producto);

            com.opencart.pages.ProductPage productPage = new com.opencart.pages.ProductPage(driver);
            productPage.selectFirstOptionIfAvailable();
            productPage.setQuantity(cantidadStr);
            productPage.addToCart();
        }

        // Construir mapa de productos esperados (nombre -> cantidad total)
        Map<String, Integer> esperados = new HashMap<>();

        for (int i = startRow; i < datos.size(); i++) {
            String[] fila = datos.get(i);
            if (fila == null || fila.length < 4) continue;

            boolean todasVacias = true;
            for (int c = 0; c < 4; c++) {
                if (fila[c] != null && !fila[c].trim().isEmpty()) {
                    todasVacias = false;
                    break;
                }
            }
            if (todasVacias) continue;

            String producto = fila[2].trim();
            String cantidadStr = fila[3].trim();
            int cantidad = (int) Double.parseDouble(cantidadStr);

            esperados.merge(producto, cantidad, Integer::sum);
        }

        // Ir al carrito y leer productos reales
        driver.get(baseUrl + "/index.php?route=checkout/cart");

        CartPage cartPage = new CartPage(driver);
        List<CartPage.CartItem> items = cartPage.getCartItems();

        Map<String, Integer> reales = new HashMap<>();
        System.out.println("Productos reales en el carrito:");
        for (CartPage.CartItem item : items) {
            int qty = Integer.parseInt(item.quantity);

            // Normalizar el nombre: quitar espacios, tomar solo la primera línea
            String name = item.name == null ? "" : item.name.trim();
            int salto = name.indexOf('\n');
            if (salto != -1) {
                name = name.substring(0, salto).trim();
            }

            System.out.println(" - " + name + " x " + qty);

            reales.merge(name, qty, Integer::sum);
        }

        // Comparar y registrar en log
        for (Map.Entry<String, Integer> entry : esperados.entrySet()) {
            String prod = entry.getKey();
            int cantEsperada = entry.getValue();

            boolean existe = reales.containsKey(prod);
            int cantReal = existe ? reales.get(prod) : 0;

            String estado;
            String mensaje;

            if (!existe) {
                estado = "FALLIDO";
                mensaje = "Producto no encontrado en el carrito";
            } else if (cantReal != cantEsperada) {
                estado = "FALLIDO";
                mensaje = "Cantidad esperada " + cantEsperada + ", cantidad real " + cantReal;
            } else {
                estado = "EXITOSO";
                mensaje = "Producto y cantidad correctos en el carrito";
            }

            String[] filaLog = new String[]{
                    prod,
                    String.valueOf(cantEsperada),
                    String.valueOf(cantReal),
                    estado,
                    mensaje
            };
            ExcelUtil.escribirLog(rutaLogs, hojaLogs, filaLog);

           
            Assert.assertTrue(existe, "El producto esperado no está en el carrito: " + prod);
            Assert.assertEquals(cantReal, cantEsperada,
                    "Cantidad incorrecta para el producto " + prod);
        }
    }
}

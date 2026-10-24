# Cambio Brasil - Convertidor Reales a Pesos Uruguayos y Dólares

Aplicación Android en Jetpack Compose diseñada especialmente para viajes a Brasil, ofreciendo conversión simultánea instantánea entre Reales Brasileños (BRL), Pesos Uruguayos (UYU) y Dólares Estadounidenses (USD), con interfaz táctil extra grande, teclado numérico/calculadora integrado y banderas nacionales de alta fidelidad.

## Decisiones Críticas y Preferencias del Usuario

> [!IMPORTANT]
> A partir de tus respuestas en la fase de aclaración, hemos alineado las siguientes decisiones principales:

- **Cotizaciones con ajuste manual editable**: Se incluyen valores de referencia actualizados con la posibilidad de editarlos en cualquier momento (ideal para cuando cambiaste dinero en una casa de cambio específica o quieres contemplar comisiones de tarjeta) y soporte 100% offline.
- **Teclado numérico y calculadora en pantalla**: Teclado integrado de gran tamaño (números grandes y botones táctiles cómodos) con operadores de suma, resta y multiplicación para sumar platos de restaurante o precios de compras al instante sin salir de la app ni usar el teclado del sistema.
- **Enfoque directo y sin distracciones**: Conversión simultánea en tiempo real en una pantalla principal limpia, sin sobrecarga de menús secundarios ni herramientas innecesarias.

---

## 1. Visión General del Producto

- **Propósito**: Facilitar la vida al turista uruguayo en Brasil, permitiendo saber en 1 segundo cuánto cuesta cualquier producto o servicio en su moneda local (UYU) y en dólares (USD).
- **Público Objetivo**: Viajeros uruguayos en Brasil que necesitan consultar precios rápidamente en supermercados, quioscos de playa, restaurantes o tiendas sin perder tiempo ni equivocarse con los ceros.
- **Valor Clave**: Interfaz con números gigantes, tarjetas con banderas de Brasil, Uruguay y Estados Unidos, y un teclado/calculadora de respuesta inmediata que funciona sin requerir conexión a internet.

---

## 2. Experiencia de Usuario y Diseño Visual

### Flujo de Interacción
1. **Pantalla Principal Dividida en Dos Áreas**:
   - **Área Superior (Pantallas de Moneda)**:
     - **Tarjeta Brasil 🇧🇷 (R$ Real)**: Moneda base destacada con texto grande, borde iluminado y bandera brasileña.
     - **Tarjeta Uruguay 🇺🇾 ($U Peso Uruguayo)**: Resultado convertido inmediatamente en números legibles con bandera uruguaya.
     - **Tarjeta EE.UU. 🇺🇸 (US$ Dólar)**: Conversión en dólares para comparar con tarjeta de crédito o presupuesto general.
     - *Tocar cualquiera de las tarjetas permite cambiar la moneda activa de entrada si se desea*.
   - **Área Inferior (Teclado Numérico y Calculadora)**:
     - Botones grandes (7-8-9, 4-5-6, 1-2-3, 0, punto decimal).
     - Operaciones rápidas (`+`, `-`, `×`, `÷`, `=`) para poder calcular cuentas (ej. `25 + 18 + 12`).
     - Botones `C` (Borrar todo) y `⌫` (Borrar dígito).
     - Chips de acceso rápido (`+10`, `+50`, `+100` BRL) para agilizar consultas frecuentes.
2. **Diálogo de Cotizaciones y Ajustes**:
   - Accesible desde un botón en la barra superior ("Cotizaciones").
   - Permite ajustar:
     - 1 BRL = X UYU (valor inicial sugerido: ~7.95 UYU).
     - 1 USD = X BRL (valor inicial sugerido: ~5.05 BRL).
     - 1 USD = X UYU (valor inicial sugerido: ~40.10 UYU).
   - Botón para actualizar vía internet (si hay datos disponibles) o restablecer valores estándar de viaje.

### Identidad Visual y Estilo
- **Paleta de Colores**:
  - Tema cálido y moderno inspirado en Brasil y el Cono Sur:
    - Fondo: Tonalidades oscuras modernas o claras de alto contraste para máxima legibilidad bajo el sol.
    - Acento Brasil: Verde tropical (`#10B981`) y Amarillo cálido (`#F59E0B`).
    - Acento Uruguay: Azul celeste suave (`#0284C7` / `#38BDF8`).
    - Acento Dólar: Verde esmeralda suave (`#22C55E`).
- **Banderas de Países**:
  - Representaciones vectoriales nítidas en Compose para Brasil 🇧🇷, Uruguay 🇺🇾 y Estados Unidos 🇺🇸, que se ven perfectas en cualquier densidad de pantalla y sin depender de descargas externas.
- **Tipografía y Legibilidad**:
  - Números en display monoespaciados o sans-serif gruesos para que las cifras no salten al escribir.
  - Tamaño de texto dinámico que se adapta si el número tiene muchos dígitos para que nunca se corte.

---

## 3. Decisiones Técnicas y Arquitectura

```
┌────────────────────────────────────────────────────────┐
│                      MainActivity                      │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│                    ExchangeViewModel                   │
│  - inputExpression: StateFlow<String>                  │
│  - activeCurrency: StateFlow<CurrencyType>             │
│  - exchangeRates: StateFlow<ExchangeRates>             │
│  - convertedAmounts: StateFlow<ConversionResult>       │
└─────────────┬────────────────────────────┬─────────────┘
              │                            │
┌─────────────▼───────────────┐ ┌──────────▼─────────────┐
│       UI Components         │ │     Data & Storage     │
│ - CurrencyDisplayCards      │ │ - RatesPreferences     │
│   (BRL, UYU, USD + Flags)   │ │   (SharedPreferences)  │
│ - LargeNumericKeypad        │ │ - RatesRepository      │
│   (Digits + Calc Operators) │ │ - Free Exchange API    │
│ - EditRatesDialog           │ │   (Retrofit/OkHttp)    │
└─────────────────────────────┘ └────────────────────────┘
```

### Componentes Principales:
1. **`model/Currency.kt`**:
   - `CurrencyType`: Enum (`BRL`, `UYU`, `USD`) con nombre, símbolo (`R$`, `$U`, `US$`), y bandera.
   - `ExchangeRates`: Datos de conversión (BRL a UYU, BRL a USD, etc.) con marcas de tiempo.
2. **`data/RatesRepository.kt`**:
   - Persistencia local en SharedPreferences para garantizar funcionamiento sin conexión.
   - Servicio ligero para obtener cotizaciones actualizadas cuando haya conexión a internet.
3. **`ui/components/CountryFlags.kt`**:
   - Dibujo vectorial nativo Compose de las 3 banderas nacionales para nitidez absoluta y cero dependencia externa.
4. **`ui/components/CurrencyCards.kt`**:
   - Tarjetas de conversión con tipografía gigante, símbolos claros y selector de moneda activa.
5. **`ui/components/NumericKeypad.kt`**:
   - Teclado con elevación, retroalimentación táctil, botones generosos (mínimo 56dp) y calculadora aritmética básica.
6. **`ui/components/RatesDialog.kt`**:
   - Editor emergente para ajustar cotizaciones de compra/venta o tasas bancarias de tarjetas.

---

## 4. Plan de Ejecución Paso a Paso

1. **Configuración de Recursos y Metadatos**:
   - Actualizar `metadata.json` con nombre `Cambio Brasil` y descripción relevante.
   - Actualizar `strings.xml` con los textos de la app en español.
2. **Modelo y Repositorio de Cotizaciones**:
   - Crear modelos de datos y repositorio con cotizaciones por defecto (BRL, UYU, USD) y persistencia local.
   - Implementar cálculo aritmético y conversor bidireccional instantáneo.
3. **Diseño de Banderas Vectoriales e Iconografía**:
   - Crear componentes gráficos para las banderas de Brasil, Uruguay y Estados Unidos.
4. **Construcción de la Interfaz de Usuario**:
   - Implementar las tarjetas de visualización con números gigantes.
   - Crear el teclado numérico de pantalla completa con calculadora.
   - Crear el diálogo de configuración y personalización de cotizaciones.
5. **Verificación y Compilación**:
   - Compilar con `compile_applet` para asegurar que el proyecto se ejecute sin errores.

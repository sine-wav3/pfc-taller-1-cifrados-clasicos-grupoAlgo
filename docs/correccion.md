# Informe de corrección: puntos 1 y 2 (cifrado César)

Se demuestra que `cesar` y `cesarCola` cumplen su especificación para toda entrada, usando inducción estructural sobre la longitud del mensaje.

## 1. Notación

- $\Sigma = \{\texttt{a}, \dots, \texttt{z}\}$ son las letras minúsculas, y $\mathcal{C}$ es el conjunto de todos los caracteres.
- $\mathcal{C}^*$ es el conjunto de cadenas; $\varepsilon$ es la cadena vacía y $|m|$ la longitud de $m$.
- $c \cdot m'$ es la cadena con primer carácter $c$ y resto $m'$ (en Scala, `m.head` y `m.tail`).
- $u \mathbin{+\!\!+} v$ es la concatenación de $u$ y $v$.
- $\mathrm{pos}(c) = c - \texttt{'a'} \in \{0, \dots, 25\}$ para $c \in \Sigma$, y $\mathrm{chr}$ es su inversa.
- $x \bmod 26 \in \{0, \dots, 25\}$ denota el módulo matemático (siempre no negativo), incluso si $x < 0$.

### Desplazamiento de un carácter

Para $k \in \mathbb{Z}$ se define $f_k : \mathcal{C} \to \mathcal{C}$:

$$
f_k(c) =
\begin{cases}
\mathrm{chr}\big((\mathrm{pos}(c) + k) \bmod 26\big) & \text{si } c \in \Sigma \\
c & \text{si } c \notin \Sigma
\end{cases}
$$

### Especificación

El cifrado César de $m = c_0 c_1 \cdots c_{n-1}$ con desplazamiento $k$ es:

$$
C(m, k) = f_k(c_0) \mathbin{+\!\!+} f_k(c_1) \mathbin{+\!\!+} \cdots \mathbin{+\!\!+} f_k(c_{n-1})
$$

Equivalentemente, de forma recursiva:

$$
C(\varepsilon, k) = \varepsilon, \qquad C(c \cdot m', k) = f_k(c) \mathbin{+\!\!+} C(m', k)
$$

## 2. Lemas sobre la auxiliar `desplazar`

La auxiliar del código calcula `((c - 'a' + k) % 26 + 26) % 26 + 'a'`. Hay que mostrar que coincide con $f_k$.

**Lema 1 (módulo no negativo).** Para todo $x \in \mathbb{Z}$, sea $r = x \,\%\, 26$ el resto de Scala. Entonces $((r + 26) \,\%\, 26) = x \bmod 26$.

*Demostración.* En Scala, $r$ tiene el signo de $x$ y cumple $|r| < 26$ y $r \equiv x \pmod{26}$. Luego $r + 26 \in (0, 52)$, es positivo, y $r + 26 \equiv x \pmod{26}$. Para un valor positivo, `%` coincide con el módulo matemático, así que $(r + 26) \,\%\, 26$ es el único representante de la clase de $x$ en $\{0, \dots, 25\}$, es decir $x \bmod 26$. $\blacksquare$

**Lema 2 (`desplazar` implementa $f_k$).** Para todo carácter $c$, `desplazar(c)` $= f_k(c)$.

*Demostración.* Si $c \in \Sigma$ (condición `c >= 'a' && c <= 'z'`), el código evalúa $\mathrm{chr}\big((\mathrm{pos}(c) + k) \bmod 26\big)$ por el Lema 1, que es $f_k(c)$. Si $c \notin \Sigma$, devuelve $c$, igual que $f_k$. En particular, las mayúsculas, tildes, `ñ`, dígitos, espacios y signos pasan sin cambio. $\blacksquare$

**Lema 3 (periodicidad e inversa).** Para toda letra $c \in \Sigma$:

1. $f_{k+26}(c) = f_k(c)$.
2. $f_{-k}(f_k(c)) = c$.

*Demostración.* (1) $(p + k + 26) \bmod 26 = (p + k) \bmod 26$. (2) $\big((p + k) \bmod 26 - k\big) \bmod 26 = p \bmod 26 = p$. Para $c \notin \Sigma$ ambas igualdades son triviales. $\blacksquare$

Estos resultados justifican los casos del enunciado: $k = 29$ equivale a $k = 3$, y `cesar("fdvd", -3)` devuelve `"casa"`.

## 3. Corrección de `cesar`

```scala
def cesar(m: Mensaje, k: Int): Mensaje =
  if (m.isEmpty) "" else desplazar(m.head).toString + cesar(m.tail, k)
```

**Teorema 1.** Para todo $m \in \mathcal{C}^*$ y $k \in \mathbb{Z}$, `cesar(m, k)` $= C(m, k)$.

*Demostración.* Por inducción sobre $n = |m|$.

- **Caso base ($n = 0$).** $m = \varepsilon$, el código devuelve `""` $= \varepsilon = C(\varepsilon, k)$.
- **Hipótesis inductiva.** Para toda cadena de longitud $n$, `cesar` devuelve $C(\cdot, k)$.
- **Paso inductivo.** Sea $|m| = n + 1$ con $m = c \cdot m'$ y $|m'| = n$. El código devuelve `desplazar(c)` $\mathbin{+\!\!+}$ `cesar(m', k)`. Por el Lema 2, `desplazar(c)` $= f_k(c)$, y por hipótesis inductiva `cesar(m', k)` $= C(m', k)$. Entonces el resultado es $f_k(c) \mathbin{+\!\!+} C(m', k) = C(c \cdot m', k) = C(m, k)$. $\blacksquare$

**Terminación.** En cada llamada, $|m|$ disminuye en 1 (se pasa `m.tail`), y la recursión se detiene al llegar a $|m| = 0$. Por tanto, la función termina tras exactamente $|m| + 1$ llamadas.

### Cómo se encadenan los llamados

Ejemplo con $m = \texttt{"abc"}$ y $k = -1$ (resultado esperado: `"zab"`):

$$
\begin{aligned}
\texttt{cesar("abc", -1)} &= f_{-1}(\texttt{a}) \mathbin{+\!\!+} \texttt{cesar("bc", -1)} \\
&= \texttt{z} \mathbin{+\!\!+} \big(f_{-1}(\texttt{b}) \mathbin{+\!\!+} \texttt{cesar("c", -1)}\big) \\
&= \texttt{z} \mathbin{+\!\!+} \big(\texttt{a} \mathbin{+\!\!+} (f_{-1}(\texttt{c}) \mathbin{+\!\!+} \texttt{cesar("", -1)})\big) \\
&= \texttt{z} \mathbin{+\!\!+} \big(\texttt{a} \mathbin{+\!\!+} (\texttt{b} \mathbin{+\!\!+} \varepsilon)\big) \\
&= \texttt{"zab"}
\end{aligned}
$$

Cada llamada **espera** el resultado de la siguiente para poder concatenar; por eso el valor se construye al regresar, de adentro hacia afuera. Los valores usados salen de $f_{-1}(\texttt{a}) = \mathrm{chr}((0 - 1) \bmod 26) = \texttt{z}$, $f_{-1}(\texttt{b}) = \texttt{a}$ y $f_{-1}(\texttt{c}) = \texttt{b}$.

## 4. Corrección de `cesarCola`

```scala
@tailrec
final def cesarCola(m: Mensaje, k: Int, acc: Mensaje = ""): Mensaje =
  if (m.isEmpty) acc else cesarCola(m.tail, k, acc + desplazar(m.head))
```

Aquí el resultado parcial viaja en `acc`. Se enuncia un **invariante** que relaciona el acumulador con lo que falta por procesar.

**Lema 4 (invariante del acumulador).** Para todo $m, acc \in \mathcal{C}^*$ y $k \in \mathbb{Z}$:

$$
\texttt{cesarCola}(m, k, acc) = acc \mathbin{+\!\!+} C(m, k)
$$

*Demostración.* Por inducción sobre $n = |m|$, con $acc$ arbitrario.

- **Caso base ($n = 0$).** El código devuelve $acc$. Como $C(\varepsilon, k) = \varepsilon$, se tiene $acc \mathbin{+\!\!+} C(\varepsilon, k) = acc$. ✓
- **Hipótesis inductiva.** Vale para toda cadena de longitud $n$ y **cualquier** acumulador.
- **Paso inductivo.** Sea $m = c \cdot m'$ con $|m'| = n$. El código llama a $\texttt{cesarCola}(m', k, acc \mathbin{+\!\!+} f_k(c))$, usando el Lema 2. Por hipótesis inductiva, con acumulador $acc \mathbin{+\!\!+} f_k(c)$:
  $$
  \begin{aligned}
  \texttt{cesarCola}(m, k, acc) &= (acc \mathbin{+\!\!+} f_k(c)) \mathbin{+\!\!+} C(m', k) \\
  &= acc \mathbin{+\!\!+} \big(f_k(c) \mathbin{+\!\!+} C(m', k)\big) \qquad \text{(asociatividad de } \mathbin{+\!\!+}\text{)} \\
  &= acc \mathbin{+\!\!+} C(c \cdot m', k) \;=\; acc \mathbin{+\!\!+} C(m, k) \qquad \blacksquare
  \end{aligned}
  $$

La hipótesis inductiva debe valer para **cualquier** acumulador, porque en cada paso el acumulador cambia.

**Teorema 2.** Para todo $m$ y $k$: $\texttt{cesarCola}(m, k) = \texttt{cesar}(m, k)$.

*Demostración.* Con el valor por defecto $acc = \varepsilon$, el Lema 4 da $\texttt{cesarCola}(m, k, \varepsilon) = \varepsilon \mathbin{+\!\!+} C(m, k) = C(m, k)$, y por el Teorema 1 $C(m, k) = \texttt{cesar}(m, k)$. $\blacksquare$

**Terminación.** Igual que en `cesar`, $|m|$ disminuye en 1 en cada llamada, así que se alcanza $|m| = 0$ y la función termina tras $|m| + 1$ llamadas.

**Posición de cola.** En la rama recursiva, la llamada a `cesarCola` es la expresión completa que se devuelve: no hay ninguna operación pendiente después de ella (la concatenación se hace *antes*, al calcular el argumento `acc`). Eso es lo que permite aplicar `@tailrec` y que el proceso sea iterativo.

### Cómo se encadenan los llamados

Con el mismo ejemplo, $m = \texttt{"abc"}$ y $k = -1$. En cada paso, el invariante $acc \mathbin{+\!\!+} C(m, k) = \texttt{"zab"}$ se conserva:

| Llamada | $m$ | $acc$ | $acc \mathbin{+\!\!+} C(m, -1)$ |
|---|---|---|---|
| `cesarCola("abc", -1, "")` | `abc` | `""` | `"" ++ "zab"` = `"zab"` |
| `cesarCola("bc", -1, "z")` | `bc` | `z` | `"z" ++ "ab"` = `"zab"` |
| `cesarCola("c", -1, "za")` | `c` | `za` | `"za" ++ "b"` = `"zab"` |
| `cesarCola("", -1, "zab")` | `""` | `zab` | `"zab" ++ ""` = `"zab"` |

En la última fila se llega al caso base y se devuelve $acc = \texttt{"zab"}$. Se ve cómo la parte ya cifrada crece en `acc` mientras la parte pendiente `m` se consume, y su unión siempre es el resultado final.

## 5. Conclusión

Por los Teoremas 1 y 2, ambas funciones calculan exactamente $C(m, k)$ para todo mensaje y todo $k \in \mathbb{Z}$ (positivo, negativo o mayor que 26), y copian sin cambio todo carácter que no sea letra minúscula. Se diferencian solo en el proceso que generan: `cesar` deja una concatenación pendiente por letra, mientras que `cesarCola` mantiene el resultado parcial en el acumulador, como lo establece el Lema 4.

## 6. Corrección de `frecuencias` (punto 3)

### 6.1. Notación y especificación

- $N(l, m)$ es el número de veces que la letra $l \in \Sigma$ aparece en $m$.
- $A: \Sigma \to \mathbb{N}$ es un acumulador; una letra ausente vale $0$.
- $\mathrm{Sort}$ ordena pares $(l, n)$ de forma que $(l_1, n_1)$ va antes que $(l_2, n_2)$ si $n_1 > n_2$, o si $n_1 = n_2$ y $l_1 < l_2$.

La especificación es:

$$
F(m) = \mathrm{Sort}\big(\{(l, N(l, m)) \mid l \in \Sigma,\ N(l, m) > 0\}\big)
$$

### 6.2. Invariante de `contar`

**Lema 5.** Para todo $resto \in \mathcal{C}^*$ y todo acumulador $A$, `contar(resto, A)` devuelve $A'$ tal que, para toda $l \in \Sigma$:

$$
A'(l) = A(l) + N(l, resto)
$$

*Demostración.* Por inducción sobre $n = |resto|$, con $A$ arbitrario.

- **Caso base ($n = 0$).** Devuelve $A$, y $N(l, \varepsilon) = 0$, así que $A'(l) = A(l)$. ✓
- **Hipótesis inductiva.** Vale para toda cadena de longitud $n$ y **cualquier** acumulador.
- **Paso inductivo.** Sea $resto = c \cdot r'$ con $|r'| = n$.
  - Si $c \in \Sigma$, se llama con $A_1 = A[c \mapsto A(c) + 1]$. Por hipótesis inductiva, $A'(l) = A_1(l) + N(l, r')$. Para $l = c$ da $A(c) + 1 + N(c, r') = A(c) + N(c, c \cdot r')$; para $l \neq c$ da $A(l) + N(l, r') = A(l) + N(l, c \cdot r')$.
  - Si $c \notin \Sigma$, se llama con $A_1 = A$ y $N(l, c \cdot r') = N(l, r')$ para toda $l \in \Sigma$, así que el resultado es el mismo. $\blacksquare$

La hipótesis debe valer para **cualquier** acumulador, porque cambia en cada llamada.

### 6.3. Corrección de `frecuencias`

**Teorema 3.** Para todo $m \in \mathcal{C}^*$, `frecuencias(m)` $= F(m)$.

*Demostración.* Con $A_0 = \emptyset$ (todo vale $0$), el Lema 5 da $A'(l) = N(l, m)$. El `Map` solo contiene las letras que se incrementaron al menos una vez, es decir, las que cumplen $N(l, m) > 0$. Luego `toList` produce exactamente el conjunto de pares de $F(m)$. Como las letras son distintas, el criterio $(-n, l)$ es un orden total sin empates, así que `sortBy` produce una única lista, la de $\mathrm{Sort}$. $\blacksquare$

**Terminación.** En cada llamada $|resto|$ disminuye en 1 y se alcanza $|resto| = 0$; hay $|m| + 1$ llamadas.

**Posición de cola.** En cada rama recursiva la llamada es la expresión completa devuelta; `updated` se evalúa antes, al calcular el argumento. Por eso `@tailrec` es válido.

### 6.4. Cómo se encadenan los llamados

Con $m = \texttt{"casa"}$, el invariante $A(l) + N(l, resto)$ se conserva en cada llamada:

| Llamada | $resto$ | $A$ | $A(\texttt{a}) + N(\texttt{a}, resto)$ |
|---|---|---|---|
| `contar("casa", {})` | `casa` | `{}` | $0 + 2 = 2$ |
| `contar("asa", {c→1})` | `asa` | `{c→1}` | $0 + 2 = 2$ |
| `contar("sa", {c→1, a→1})` | `sa` | `{c→1, a→1}` | $1 + 1 = 2$ |
| `contar("a", {c→1, a→1, s→1})` | `a` | `{c→1, a→1, s→1}` | $1 + 1 = 2$ |
| `contar("", {c→1, a→2, s→1})` | `""` | `{c→1, a→2, s→1}` | $2 + 0 = 2$ |

En la última fila se llega al caso base y se devuelve $A$. El valor de cada letra se completa a medida que `resto` se consume, y `sortBy` lo deja en el orden pedido: `List(('a',2), ('c',1), ('s',1))`.
package taller

import scala.annotation.tailrec

/**
 * Taller 1 — cifrados clásicos con recursión.
 *
 * Solo se cifran las 26 letras minúsculas del alfabeto inglés; cualquier otro
 * carácter se copia sin cambio.
 */
class CifradosClasicos {

  type Mensaje = String
  type Clave = String

  // Una frecuencia asocia cada letra con las veces que aparece.
  type Frecuencias = List[(Char, Int)]

  val letras = 26
  val primera = 'a'.toInt

  def esMinuscula(c: Char): Boolean = c >= 'a' && c <= 'z'

  // Punto 1: responsable Daniela Franco Ibarra -------------------------------------------------------------------

  /** César con recursión lineal: una operación pendiente por letra. */
  def cesar(m: Mensaje, k: Int): Mensaje = {
    def desplazar(c: Char): Char =
      if (c >= 'a' && c <= 'z') ((((c - 'a' + k) % 26) + 26) % 26 + 'a').toChar
      else c

    if (m.isEmpty) ""
    else desplazar(m.head).toString + cesar(m.tail, k)
  }

  // Punto 2: responsable Daniela Franco Ibarra -------------------------------------------------------------------

  /**
   * El mismo César como proceso iterativo: espacio constante.
   * Cuando la función esté escrita, anótela con @tailrec: el compilador
   * comprueba que la llamada recursiva sea lo último que hace.
   */
  @tailrec
  final def cesarCola(m: Mensaje, k: Int, acc: Mensaje = ""): Mensaje = {
    def desplazar(c: Char): Char =
      if (c >= 'a' && c <= 'z') ((((c - 'a' + k) % 26) + 26) % 26 + 'a').toChar
      else c

    if (m.isEmpty) acc
    else cesarCola(m.tail, k, acc + desplazar(m.head))
  }

  // Punto 3: responsable Dayan Stefany Marulanda -------------------------------------------------------------------

  /**
   * Cuenta las letras minúsculas del mensaje, de mayor a menor frecuencia y,
   * en empate, en orden alfabético. El recorrido es recursivo de cola.
   */

  def frecuencias(m: Mensaje): Frecuencias = {
    @tailrec
    def contar(resto: Mensaje, acc: Map[Char, Int]): Map[Char, Int] =
      if (resto.isEmpty) acc
      else if (esMinuscula(resto.head))
        contar(resto.tail, acc.updated(resto.head, acc.getOrElse(resto.head, 0) + 1))
      else contar(resto.tail, acc)

    contar(m, Map.empty[Char, Int]).toList.sortBy(p => (-p._2, p._1))
  }

  // Punto 4: responsable Daniela Franco, Dayan Stefany, Juan Alejandro -------------------------------------------------------------------

  /**
   * Supone que la letra más frecuente del mensaje cifrado es la 'e' del
   * original y devuelve la distancia entre las dos. Sin letras, cero.
   */
  def desplazamientoProbable(m: Mensaje): Int = {
    val frecuencias = "abcdefghijklmnopqrstuvwxyz"
    val letras = m.toLowerCase.filter(_.isLetter)

    if (letras.isEmpty) {
      0
    } else {
      val conteos = frecuencias.map(letra => (letra, letras.count(_ == letra)))
      val letraMasFrecuente = conteos.maxBy{
        case (letra, cantidad) => (cantidad, -letra)
      }._1
      (letraMasFrecuente - 'e' + 26) % 26
    }
  }

  def romperCesar(m: Mensaje): Mensaje = {
    val desplazamiento = desplazamientoProbable(m)

    cesar(m, -desplazamiento)
  }

  // Punto 5: responsable Juan Alejandro Marquez -------------------------------------------------------------------

  /**
   * Cuántos mensajes de longitud n se forman con a letras sin dos iguales
   * seguidas.
   */
  def combinaciones(n: Int, a: Int): BigInt = {
    if (n == 0){
      BigInt(1)
    } else if (n == 1){
      BigInt(a)
    } else {
     BigInt(a - 1) * combinaciones(n - 1, a)
    }
  }

  /**
   * Vigenère: cada letra se corre según la letra de la clave que le toca. Lo
   * que no es letra minúscula se copia y no consume clave.
   */
  def vigenere(m: Mensaje, clave: Clave): Mensaje = {

    def cifrar(mensaje: String, posicionClave: Int): String = {
      if (mensaje.isEmpty) {
        ""
      } else {
        val caracter = mensaje.head

        if (caracter >= 'a' && caracter <= 'z') {
          val letraClave = clave(posicionClave % clave.length)
          val desplazamiento = letraClave - 'a'
          val nuevaLetra = ((caracter - 'a' + desplazamiento) % 26 + 'a').toChar

          nuevaLetra.toString + cifrar(mensaje.tail, posicionClave + 1)
        } else {
          caracter.toString + cifrar(mensaje.tail, posicionClave)
        }
      }
    }

    if (clave.isEmpty) {
      m
    } else {
      cifrar(m, 0)
    }
  }
}

package taller

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

/**
 * Cada ejemplo del enunciado es una prueba. Si el enunciado promete un valor,
 * aquí se comprueba que la solución lo produce.
 */
@RunWith(classOf[JUnitRunner])
class CifradosClasicosTest extends AnyFunSuite {

  val c = new CifradosClasicos()
  import c._

  // Punto 1: ejemplos del enunciado -------------------------------------------

  test("cesar: casa con 3 da fdvd") { assert(cesar("casa", 3) == "fdvd") }
  test("cesar: fdvd con -3 vuelve a casa") { assert(cesar("fdvd", -3) == "casa") }
  test("cesar: hola mundo con 1") { assert(cesar("hola mundo", 1) == "ipmb nvoep") }
  test("cesar: zzz con 1 da aaa") { assert(cesar("zzz", 1) == "aaa") }
  test("cesar: 29 es lo mismo que 3") { assert(cesar("abc", 29) == "def") }
  test("cesar: el mensaje vacío sale vacío") { assert(cesar("", 5) == "") }

  // Punto 1: pruebas propias ------------------------------------------------
  test("cesar: el desplazamiento da la vuelta al final del alfabeto") { assert(cesar("xyz", 3) == "abc")}
  test("cesar: desplazamiento negativo da la vuelta al principio") { assert(cesar("abc", -1) == "zab") }
  test("cesar: un desplazamiento de 52 equivale a 0") { assert(cesar("abc", 52) == "abc") }
  test("cesar: tildes, eñe y espacios pasan sin cambio") { assert(cesar("ñandú café", 1) == "ñboeú dbgé")}
  test("cesar: desplazamiento negativo mayor a 26 en valor absoluto") { assert(cesar("hola", -27) == "gnkz") }

  // Punto 2 -------------------------------------------------------------------

    test("cesarCola: casa con 3 da fdvd") {
      assert(cesarCola("casa", 3) == "fdvd")
    }
    test("cesarCola: hola mundo con 1") {
      assert(cesarCola("hola mundo", 1) == "ipmb nvoep")
    }
    test("cesarCola: con 0 el mensaje no cambia") {
      assert(cesarCola("abc", 0) == "abc")
    }

    test("cesarCola: da lo mismo que la versión lineal") {
      val casos = List(("casa", 3), ("hola mundo", 1), ("zzz", 1), ("abc", 29),
        ("", 5), ("ab, 12!", -4))
      assert(casos.forall { case (m, k) => cesarCola(m, k) == cesar(m, k) })
    }

    test("cesarCola: aguanta un mensaje largo sin desbordar la pila") {
      val largo = "abcdefghij" * 20000
      assert(cesarCola(largo, 1).length == largo.length)
    }

  // Punto 2: pruebas propias -----------------------------------------------------

  test("cesarCola: el desplazamiento da la vuelta al final del alfabeto") { assert(cesarCola("xyz", 3) == "abc") }

  test("cesarCola: desplazamiento negativo da la vuelta al principio") { assert(cesarCola("abc", -1) == "zab") }

  test("cesarCola: frase con espacio") { assert(cesarCola("mensaje secreto", 5) == "rjsxfoj xjhwjyt") }

  test("cesarCola: mayúsculas, dígitos y puntuación pasan sin cambio") { assert(cesarCola("HOLA 123 mundo!", 1) == "HOLA 123 nvoep!") }

  test("cesarCola: da el mismo resultado que cesar") {
    val mensajes = List("hola mundo", "zzz yyy", "a1b2c3", "")
    val ks = List(-30, -3, 0, 7, 29)
    assert(mensajes.forall(m => ks.forall(k => cesarCola(m, k) == cesar(m, k))))
  }

  test("cesarCola: un mensaje largo no desborda la pila") {
    val largo = "a" * 20000
    assert(cesarCola(largo, 1) == "b" * 20000)
  }

    // Punto 3 -------------------------------------------------------------------

    test("frecuencias: casa") {
      assert(frecuencias("casa") == List(('a', 2), ('c', 1), ('s', 1)))
    }

    test("frecuencias: aabbbc") {
      assert(frecuencias("aabbbc") == List(('b', 3), ('a', 2), ('c', 1)))
    }

    test("frecuencias: hola mundo") {
      assert(frecuencias("hola mundo") ==
        List(('o', 2), ('a', 1), ('d', 1), ('h', 1), ('l', 1), ('m', 1),
          ('n', 1), ('u', 1)))
    }

    test("frecuencias: el mensaje vacío no tiene letras") {
      assert(frecuencias("") == List())
    }

    test("frecuencias: un mensaje sin letras no tiene frecuencias") {
      assert(frecuencias("123 !?") == List())
    }

    test("frecuencias: en empate manda el orden alfabético") {
      assert(frecuencias("ba") == List(('a', 1), ('b', 1)))
    }

    // Punto 3: Pruebas propias

    test("frecuencias: perro") {
      assert(frecuencias("perro") == List(('r', 2), ('e', 1), ('o', 1), ('p', 1)))
    }

    test("frecuencias: banana") {
      assert(frecuencias("banana") == List(('a', 3), ('n', 2), ('b', 1)))
    }

    test("frecuencias: programacion") {
      assert(frecuencias("programacion") ==
        List(('a', 2), ('o', 2), ('r', 2), ('c', 1), ('g', 1),
          ('i', 1), ('m', 1), ('n', 1), ('p', 1)))
    }

    test("frecuencias: mensaje con espacios") {
      assert(frecuencias("hola mundo") ==
        List(('o', 2), ('a', 1), ('d', 1), ('h', 1), ('l', 1), ('m', 1),
          ('n', 1), ('u', 1)))
    }

    test("frecuencias: empate alfabetico") {
      assert(frecuencias("dcba") ==
        List(('a', 1), ('b', 1), ('c', 1), ('d', 1)))
    }

    // Punto 4 -------------------------------------------------------------------

    test("desplazamientoProbable: h está 3 después de e") {
      assert(desplazamientoProbable("h") == 3)
    }

    test("desplazamientoProbable: hhhaa, con h como la más frecuente") {
      assert(desplazamientoProbable("hhhaa") == 3)
    }

    test("desplazamientoProbable: sin letras da 0") {
      assert(desplazamientoProbable("123") == 0)
    }

    test("desplazamientoProbable: en empate manda la primera alfabéticamente") {
      // 'a' y 'h' aparecen tres veces; gana 'a', que está 22 después de 'e'.
      assert(desplazamientoProbable("hhhaaa") == 22)
    }

    test("romperCesar: recupera un mensaje con suficientes letras e") {
      val original = "el mensaje secreto"
      assert(romperCesar(cesar(original, 7)) == original)
    }

    test("romperCesar: el método falla cuando la e no es la más frecuente") {
      // En este mensaje la letra más frecuente es la 'a', no la 'e'.
      val original = "cada casa amarilla"
      assert(romperCesar(cesar(original, 7)) != original)
    }

    // Punto 4: Pruebas propias

    test("desplazamientoProbable: la letra h tiene desplazamiento 3") {
      assert(desplazamientoProbable("hhh") == 3)
    }

    test("desplazamientoProbable: la letra z tiene desplazamiento 21") {
      assert(desplazamientoProbable("zzz") == 21)
    }

    test("desplazamientoProbable: mensaje sin letras") {
      assert(desplazamientoProbable("456 !?") == 0)
    }

    test("romperCesar: recupera un mensaje cifrado con desplazamiento 5") {
      val original = "este es un mensaje secreto"
      assert(romperCesar(cesar(original, 5)) == original)
    }

    test("romperCesar: no recupera correctamente un mensaje si la frecuencia engaña") {
      val original = "aaaa bbbb"
      assert(romperCesar(cesar(original, 4)) != original)
    }

    // Punto 5: Combinaciones -------------------------------------------------------------------

    test("combinaciones: con longitud 0 hay un mensaje, el vacío") {
      assert(combinaciones(0, 26) == BigInt(1))
    }

    test("combinaciones: con longitud 1 hay tantos como letras") {
      assert(combinaciones(1, 26) == BigInt(26))
    }

    test("combinaciones: 3 letras sobre 26 dan 16250") {
      assert(combinaciones(3, 26) == BigInt(16250))
    }

    test("combinaciones: 2 letras sobre un alfabeto de 2 dan 2") {
      assert(combinaciones(2, 2) == BigInt(2))
    }

    test("combinaciones: crece según la recurrencia") {
      assert(combinaciones(5, 4) == BigInt(3) * combinaciones(4, 4))
    }

    // Punto 5: Combinaciones pruebas propias

    test("combinaciones: dos letras de un alfabeto de 26") {
      // 26 * 25
      assert(combinaciones(2, 26) == BigInt(650))
    }

    test("combinaciones: longitud 4 con alfabeto de 3 letras") {
      // 3 * 2 * 2 * 2
      assert(combinaciones(4, 3) == BigInt(24))
    }

    test("combinaciones: con una sola letra solo cabe el mensaje de longitud 1") {
      assert(combinaciones(1, 1) == BigInt(1))
      assert(combinaciones(3, 1) == BigInt(0))
    }

    test("combinaciones: longitud 10 con alfabeto de 3 letras") {
      // 3 * 2^9
      assert(combinaciones(10, 3) == BigInt(1536))
    }

    test("combinaciones: resultados grandes no desbordan (BigInt)") {
      // 26 * 25^29 no cabe en un Long
      assert(combinaciones(30, 26) == BigInt(26) * BigInt(25).pow(29))
    }


    // Punto 5: vigenere
    test("vigenere: ataque con la clave sol") {
      assert(vigenere("ataque", "sol") == "shliip")
    }

    test("vigenere: hola mundo con la clave ab") {
      assert(vigenere("hola mundo", "ab") == "hplb mvneo")
    }

    test("vigenere: con la clave vacía el mensaje no cambia") {
      assert(vigenere("casa", "") == "casa")
    }

    test("vigenere: el espacio no consume letra de la clave") {
      // Sin el espacio la clave iría corrida y la m se cifraría con b.
      assert(vigenere("hola mundo", "ab").charAt(5) == 'm')
    }

    test("vigenere: con una clave de una sola letra es un César") {
      assert(vigenere("hola mundo", "d") == cesar("hola mundo", 3))
    }

    // Punto 5: vigenere pruebas propias

    test("vigenere: clave de una letra desplaza todo igual") {
      assert(vigenere("abc", "b") == "bcd")
    }

    test("vigenere: el desplazamiento da la vuelta al final del alfabeto") {
      // x+1=y, y+2=a, z+3=c
      assert(vigenere("xyz", "bcd") == "yac")
    }

    test("vigenere: la clave se repite y el espacio no consume letra") {
      // letras a,b,c,d usan b,c,b,c
      assert(vigenere("ab cd", "bc") == "bd df")
    }

    test("vigenere: mayúsculas y dígitos pasan sin consumir letra de la clave") {
      // solo la b se cifra, con la primera letra de la clave (c)
      assert(vigenere("A1b", "c") == "A1d")
    }

    test("vigenere: con clave de una letra equivale a un césar") {
      // 'd' es el desplazamiento 3
      assert(vigenere("mensaje secreto", "d") == cesar("mensaje secreto", 3))
    }
}

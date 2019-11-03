package cs1

class Complex(val real: Double, val imag: Double) {
	def +(that: Complex) = Complex(real + that.real, imag + that.imag)

	def -(that: Complex) = Complex(real - that.real, imag - that.imag)

	def *(that: Complex) = Complex(
		real * that.real - imag * that.imag, real * that.imag + imag * that.real)

	def *(x: Double) = Complex(x * real, x * imag)

	def /(x: Double) = Complex(real / x, imag / x)

	def magnitude: Double = math.sqrt(real * real + imag * imag)

	def normalize = this / magnitude
}

object Complex {
	def apply(r: Double, i: Double = 0.0) = new Complex(r, i)
}

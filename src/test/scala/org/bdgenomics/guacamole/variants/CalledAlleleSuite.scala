package org.bdgenomics.guacamole.variants

import org.apache.spark.SparkEnv
import org.bdgenomics.guacamole.{ Bases, TestUtil }
import org.bdgenomics.guacamole.TestUtil.SparkFunSuite
import org.scalatest.FunSuite

class CalledAlleleSuite extends FunSuite with SparkFunSuite {

  test("serializing called genotype") {
    val gt = CalledAllele("sample",
      "chr1",
      123456789123L,
      Allele(Seq(Bases.T), Seq(Bases.A)),
      evidence = AlleleEvidence(0.99, 15, 10, 10, 5, 60, 30))

    val serialized = TestUtil.serialize(gt)
    val deserialized = TestUtil.deserialize[CalledAllele](serialized)

    assert(gt === deserialized)
  }

  test("serializing called somatic genotype") {

    val sgt = new CalledSomaticAllele("sample",
      "chr1",
      123456789123L,
      Allele(Seq(Bases.T), Seq(Bases.A)),
      0.99 / 0.01,
      tumorEvidence = AlleleEvidence(0.99, 15, 10, 10, 5, 60, 30),
      normalEvidence = AlleleEvidence(0.01, 17, 0, 10, 0, 60, 30))

    val serialized = TestUtil.serialize(sgt)
    val deserialized = TestUtil.deserialize[CalledSomaticAllele](serialized)

    assert(sgt === deserialized)

  }

  sparkTest("serializing multi-base called somatic genotype") {

    val serializer = SparkEnv.get.serializer.newInstance()

    val sgt = new CalledSomaticAllele("sample",
      "chr1",
      123456789123L,
      Allele(Seq(Bases.T), Seq(Bases.T, Bases.A, Bases.T)),
      0.99 / 0.01,
      tumorEvidence = AlleleEvidence(0.99, 15, 10, 10, 5, 60, 30),
      normalEvidence = AlleleEvidence(0.01, 17, 0, 10, 0, 60, 30))

    val serialized = serializer.serialize(sgt)
    val deserialized = serializer.deserialize[CalledSomaticAllele](serialized)

    assert(sgt === deserialized)

  }

}

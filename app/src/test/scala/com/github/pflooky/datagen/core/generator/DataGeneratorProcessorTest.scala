package com.github.pflooky.datagen.core.generator

import com.github.pflooky.datacaterer.api.model.Constants.FORMAT
import com.github.pflooky.datacaterer.api.model.{DataCatererConfiguration, FlagsConfig, FoldersConfig}
import com.github.pflooky.datagen.core.util.SparkSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

import java.io.File
import scala.reflect.io.Directory

@RunWith(classOf[JUnitRunner])
class DataGeneratorProcessorTest extends SparkSuite {

  test("Can parse plan and tasks, then execute data generation") {
    val basePath = "src/test/resources/sample/data"
    val config = DataCatererConfiguration(
      flagsConfig = FlagsConfig(false, true, false, false, enableValidation = false),
      foldersConfig = FoldersConfig("sample/plan/simple-json-plan.yaml", "sample/task", basePath, recordTrackingFolderPath = s"$basePath/recordTracking"),
      connectionConfigByName = Map("account_json" -> Map(FORMAT -> "json"))
    )
    val dataGeneratorProcessor = new DataGeneratorProcessor(config)

    dataGeneratorProcessor.generateData()

    val generatedData = sparkSession.read
      .json(s"$basePath/generated/json/account-gen")
    val generatedCount = generatedData.count()
    assert(generatedCount > 0)
    new Directory(new File(basePath)).deleteRecursively()
  }

}

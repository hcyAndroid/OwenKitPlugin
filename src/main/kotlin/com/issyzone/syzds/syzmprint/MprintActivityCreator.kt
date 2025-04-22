package com.issyzone.syzds.syzmprint

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project

import java.io.File
import com.android.tools.idea.gradle.project.model.AndroidModuleModel
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.android.dom.manifest.Manifest


class MprintActivityCreator(
    private val activityName: String,
    private val generateLayout: Boolean,
    private val addToManifest: Boolean,
    private val currentModule: Module,
    private val currentPackageName: String,
    private val isActivity: Boolean
) {

    fun create(project: Project?) {
        project ?: return

        val file = if (isActivity) {
            createActivityFile(currentModule, currentPackageName)
        } else {
            createFragmentFile(currentModule, currentPackageName)
        }

        // 打开并聚焦到新创建的文件（放到方法最后执行）
        val virtualFile = com.intellij.openapi.vfs.LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file)
        virtualFile?.let {
            com.intellij.openapi.fileEditor.FileEditorManager.getInstance(currentModule.project).openFile(it, true)
        }
    }

    private fun createFragmentFile(module: Module, packageName: String): File {
        val className = "${activityName}Fragment"
        val layoutName = "fragment_${camelToUnderline(activityName)}"
        val bindingName = "Fragment${activityName}Binding"
        val modelName = "${activityName}Model"
        val template = """
        |package $packageName.view.fragment
        |import android.view.View
        |import ${packageName}.R
        |import ${packageName}.base.BaseMPrintFragment
        |import ${packageName}.databinding.${bindingName}
        |import ${packageName}.model.viewmodel.${modelName}
        |class $className : BaseMPrintFragment<${modelName}>() {
        |    private val binding: ${bindingName} by lazy {
        |        ${bindingName}.inflate(layoutInflater)
        |    }
        |    override fun getLayoutResId(): Int = R.layout.${layoutName}
        |    override fun getLayoutRoot(): View = binding.root
        |    override fun providerVMClass(): Class<${activityName}Model> = ${activityName}Model::class.java
        |    override fun initView() {
        |        // 初始化UI组件 
        |    }
        |    override fun initData() {
        |        // 初始化数据
        |    }
        |}
        """.trimMargin()

        // 2. 确定文件路径
        val packagePath = packageName.replace(".", "/")
        val javaDir = getMainJavaDir(module)
        val directory = "$javaDir/$packagePath/view/fragment".also {
            File(it).mkdirs() // 自动创建目录
        }
        // 4. 写入文件
        val file = File("$directory/$className.kt")
        file.writeText(template)



        if (generateLayout) {
            createLayoutFile(module, layoutName)
        }
        val templateModel = """
        |package $packageName.model.viewmodel
     
        |class ${modelName}: BaseMPModel()  {
        |
        |}
    """.trimMargin()

        val directoryModel = "$javaDir/$packagePath/model/viewmodel".also {
            File(it).mkdirs() // 自动创建目录
        }
        // 4. 写入文件
        val fileModel = File("$directoryModel/$modelName.kt")
        fileModel.writeText(templateModel)
        com.intellij.openapi.vfs.LocalFileSystem.getInstance().refreshAndFindFileByPath("$directoryModel/$modelName.kt")

        return file
    }

    fun camelToUnderline(str: String): String {
        return str.replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
    }


    private fun createActivityFile(module: Module, packageName: String): File {
        val className = "${activityName}Activity"

        val layoutName = "activity_${camelToUnderline(activityName)}"
        // val layoutName = "activity_${activityName.lowercase()}"
        val bindingName = "Activity${activityName}Binding"
        val modelName = "${activityName}Model"
        // 1. 准备模板代码
        val template = """
        |package $packageName.view.activity
        |import android.view.View
        |import ${packageName}.R
        |import ${packageName}.base.BaseMPrintActivity
        |import ${packageName}.databinding.${bindingName}
        |import ${packageName}.model.viewmodel.${modelName}
        
        |class $className : BaseMPrintActivity<${modelName}>() {
        |
        |    private val binding: ${bindingName} by lazy {
        |        ${bindingName}.inflate(layoutInflater)
        |    }
        |
        |
        |    override fun getLayoutResId(): Int = R.layout.${layoutName}
        |
        |    override fun getLayoutRoot(): View = binding.root
        |
        |    override fun providerVMClass(): Class<${activityName}Model> = ${activityName}Model::class.java
        |
        |    override fun initView() {
        |        // 初始化UI组件
        |    }
        |
        |    override fun initData() {
        |        // 初始化数据
        |    }
        |}
    """.trimMargin()

        // 2. 确定文件路径
        val packagePath = packageName.replace(".", "/")
        //       val moduleRoot = ModuleRootManager.getInstance(module).contentRoots.firstOrNull()?.path
//        val directory = "$moduleRoot/src/main/java/$packagePath/view/activity"
//        Messages.showErrorDialog("开始创建=packagePath=${packagePath}","${directory}")
//        // 3. 创建目录（如果不存在）
//        File(directory).mkdirs()
        val javaDir = getMainJavaDir(module)
        // Messages.showErrorDialog("开始创建=packagePath=${packagePath}", "${javaDir}")
        val directory = "$javaDir/$packagePath/view/activity".also {
            File(it).mkdirs() // 自动创建目录
        }

        // 4. 写入文件
        val file = File("$directory/$className.kt")
        file.writeText(template)


        // 5. 如果勾选了生成布局
        if (generateLayout) {
            createLayoutFile(module, layoutName)
        }
        //生成viewmodel
        // 1. 准备模板代码
        val templateModel = """
        |package $packageName.model.viewmodel
     
        |class ${modelName}: BaseMPModel()  {
        |
        |}
    """.trimMargin()

        val directoryModel = "$javaDir/$packagePath/model/viewmodel".also {
            File(it).mkdirs() // 自动创建目录
        }
        // 4. 写入文件
        val fileModel = File("$directoryModel/$modelName.kt")
        fileModel.writeText(templateModel)
        com.intellij.openapi.vfs.LocalFileSystem.getInstance().refreshAndFindFileByPath("$directoryModel/$modelName.kt")

//        if (addToManifest) {
//            // 6. 如果勾选了添加到 AndroidManifest.xml
//            addActivityToManifest(module,packageName,className)
//        }

        return file
    }


    private fun addActivityToManifest(module: Module, packageName: String, className: String) {
        val androidFacet = AndroidFacet.getInstance(module) ?: return
        val manifest = Manifest.getMainManifest(androidFacet) ?: return

        val application = manifest.application ?: return
        val activity = application.addActivity()
        activity.activityClass.value = JavaPsiFacade.getInstance(module.project).findClass(
            "${packageName}.view.activity.${className}", GlobalSearchScope.moduleScope(module)
        )
        activity.enabled.value = "true"

//        // 如果需要设置其他属性，例如 `exported`，可以继续设置
//        if (generateLayout) {
//            activity.exported.value = "true"
//        }
    }

    fun getMainResDir(module: Module): String {
        // 方法1：优先从 Android Facet 获取（最准确）
        ModuleRootManager.getInstance(module).sourceRoots.forEach { sourceRoot ->
            if (sourceRoot.path.contains("/src/main/res")) {
                return sourceRoot.path
            }
        }

        // 方法2：检查标准目录结构
        val contentRoot = ModuleRootManager.getInstance(module).contentRoots.firstOrNull {
            !it.path.contains("build") && File(it.path, "src/main/res").exists()
        }?.path ?: throw IllegalStateException("无法定位 res 目录")

        return "$contentRoot/src/main/res".also {
            require(File(it).exists()) { "res 目录不存在: $it" }
        }
    }

    fun getMainJavaDir(module: Module): String {
        // 方法1：优先从 sourceRoots 查找（最可靠）
        ModuleRootManager.getInstance(module).sourceRoots.forEach { sourceRoot ->
            if (sourceRoot.path.contains("/src/main/java")) {
                return sourceRoot.path
            }
        }

        // 方法2：回退方案，手动拼接路径
        val contentRoot = ModuleRootManager.getInstance(module).contentRoots.firstOrNull {
            !it.path.contains("build") && File(it.path, "src/main/java").exists()
        }?.path ?: module.moduleNioFile?.parent?.toString() ?: throw IllegalStateException("无法定位模块根目录")

        return "$contentRoot/src/main/java".also {
            require(File(it).exists()) { "路径不存在: $it" }
        }
    }


    private fun createLayoutFile(module: Module, layoutName: String) {
        val layoutDir = "${getMainResDir(module)}/layout"
        // Messages.showErrorDialog("开始创建=layoutDir=${layoutDir}", "layout")
        File(layoutDir).mkdirs()

        val layoutTemplate = """
        |<?xml version="1.0" encoding="utf-8"?>
        |<androidx.constraintlayout.widget.ConstraintLayout 
        |    xmlns:android="http://schemas.android.com/apk/res/android"
        |    xmlns:app="http://schemas.android.com/apk/res-auto"
        |    android:layout_width="match_parent"
        |    android:layout_height="match_parent">
        |
        |    <!-- 默认添加一个TextView -->
        |    <TextView
        |        android:id="@+id/tvTitle"
        |        android:layout_width="wrap_content"
        |        android:layout_height="wrap_content"
        |        android:text="Hello ${activityName}!"
        |        app:layout_constraintBottom_toBottomOf="parent"
        |        app:layout_constraintLeft_toLeftOf="parent"
        |        app:layout_constraintRight_toRightOf="parent"
        |        app:layout_constraintTop_toTopOf="parent" />
        |
        |</androidx.constraintlayout.widget.ConstraintLayout>
    """.trimMargin()

        File("$layoutDir/$layoutName.xml").writeText(layoutTemplate)
        com.intellij.openapi.vfs.LocalFileSystem.getInstance().refreshAndFindFileByPath("$layoutDir/$layoutName.xml")
    }


    fun getMainPackage(module: Module): String? {
        val androidFacet = AndroidFacet.getInstance(module) ?: return null
        return Manifest.getMainManifest(androidFacet)?.`package`?.stringValue
    }


    fun getApplicationId(module: Module): String? {
        return AndroidModuleModel.get(module)?.getApplicationId()
    }
}
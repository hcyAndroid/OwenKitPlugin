package com.issyzone.syzds.action
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import java.io.IOException

class ExcelInternationAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val project = e.project
        if (file != null) {
            // 检查文件扩展名是否为Excel格式
            if (isExcelFile(file)) {
                val result = Messages.showOkCancelDialog(
                    project,
                    "确认是这个国际化Excel文件吗？\n\n" +
                            "文件名: ${file.name}\n" +
                            "路径: ${file.path}\n" +
                            "大小: ${file.length / 1024} KB",
                    "OwenKit - 国际化",
                    Messages.getQuestionIcon()
                )
                // 如果用户点击"OK"
                if (result == Messages.OK) {
                    try {
                        // 执行国际化

                    } catch (ex: IOException) {
                        Messages.showErrorDialog(
                            project,
                            "删除文件失败: ${ex.message}",
                            "OwenKit - 删除错误"
                        )
                    }
                }
            } else {
                Messages.showErrorDialog(
                    project,
                    "请选择Excel文件 (.xls 或 .xlsx)\n" +
                            "当前文件类型: ${file.extension ?: "未知"}",
                    "OwenKit - 文件类型错误"
                )
            }
        } else {
            Messages.showErrorDialog(project, "请先选择一个文件(Excel文件)", "OwenKit - 错误")
        }
    }

    override fun update(e: AnActionEvent) {
        // 只在有文件选中时显示该Action
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isEnabledAndVisible = file != null
    }

    private fun isExcelFile(file: VirtualFile): Boolean {
        val extension = file.extension ?: return false
        return extension.equals("xls", ignoreCase = true) ||
                extension.equals("xlsx", ignoreCase = true)
    }
}
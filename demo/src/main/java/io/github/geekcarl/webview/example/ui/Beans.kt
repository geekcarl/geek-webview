package io.github.geekcarl.webview.example.ui

/**
 * Created on 2021/4/9
 * <p>
 *
 * @author geekCarl
 */
data class TestObj(
    private val name: String,
    private val content: String
) {
    override fun toString(): String {
        return "TestObj(name='$name', content='$content')"
    }
}
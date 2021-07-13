package io.github.geekcarl.bridge

/**
 * Created on 2021/4/8
 * <p>
 *
 * @author geekCarl
 */
enum class ErrorCode {

    /**
     * 成功
     */
    SUCCESS,

    /**
     * 数据转化异常
     */
    DATA_ERROR,

    /**
     * 方法未注册
     */
    UNREGISTERED;

    override fun toString(): String {
        return when {
            this == SUCCESS -> {
                "成功"
            }
            this == DATA_ERROR -> {
                "数据转化异常"
            }
            this == UNREGISTERED -> {
                "方法未注册"
            }
            else -> {
                name
            }
        }
    }
}
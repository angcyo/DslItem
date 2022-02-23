package com.angcyo.dslitem.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.dsladapter.initDslAdapter
import com.angcyo.item.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<RecyclerView>(R.id.recycler_view)?.initDslAdapter {
            render {
                DslBaseEditItem()() {
                    itemEditText = this::class.java.simpleName
                    _lastEditSelectionStart = itemEditText?.length ?: -1

                    configEditTextStyle {
                        hint = "请输入..."
                    }
                }

                DslBaseInfoItem()() {
                    itemInfoText = this::class.java.simpleName
                }

                DslBaseLabelItem()() {
                    itemLabelText = this::class.java.simpleName
                }

                DslButtonItem()() {
                    itemButtonText = this::class.java.simpleName
                }

                DslButtonItem()() {
                    itemButtonText = this::class.java.simpleName
                    configButtonStyle {
                        themeStyle()
                    }
                }
                DslButtonItem()() {
                    itemButtonText = this::class.java.simpleName
                    configButtonStyle {
                        fillStyle()
                    }
                }
                DslButtonItem()() {
                    itemButtonText = this::class.java.simpleName
                    configButtonStyle {
                        solidStyle()
                    }
                }

                DslGridItem()() {
                    itemText = this::class.java.simpleName
                    itemIcon = R.drawable.png
                }

                DslLabelEditItem()() {
                    itemLabelText = "Label"
                    itemEditText = this::class.java.simpleName
                    _lastEditSelectionStart = itemEditText?.length ?: -1

                    configEditTextStyle {
                        hint = "请输入..."
                    }
                }

                DslLabelTextItem()() {
                    itemLabelText = "Label"
                    itemText = this::class.java.simpleName
                }

                DslSwitchInfoItem()() {
                    itemInfoText = this::class.java.simpleName
                }

                DslTextInfoItem()() {
                    itemInfoText = this::class.java.simpleName
                    itemDarkText = "DarkText"
                }

                DslTextItem()() {
                    itemText = this::class.java.simpleName
                }
            }
        }
    }
}

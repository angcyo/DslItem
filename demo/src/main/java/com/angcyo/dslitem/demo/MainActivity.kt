package com.angcyo.dslitem.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.dsladapter.dpi
import com.angcyo.dsladapter.initDslAdapter
import com.angcyo.dslitem.demo.dslitem.SingleImageItem
import com.angcyo.item.DslBaseEditItem
import com.angcyo.item.DslBaseInfoItem
import com.angcyo.item.DslBaseLabelItem
import com.angcyo.item.DslButtonItem
import com.angcyo.item.DslGridItem
import com.angcyo.item.DslLabelEditItem
import com.angcyo.item.DslLabelTextItem
import com.angcyo.item.DslSwitchInfoItem
import com.angcyo.item.DslTextInfoItem
import com.angcyo.item.DslTextItem
import com.angcyo.item.style.itemEditText
import com.angcyo.item.style.renderNestedAdapter
import com.angcyo.item2.DslBannerItem

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<RecyclerView>(R.id.recycler_view)?.initDslAdapter {
            render {
                DslBannerItem()() {
                    itemHeight = 200 * dpi
                    renderNestedAdapter {
                        SingleImageItem()() {
                            itemImageUri =
                                "https://scpic.chinaz.net/files/default/imgs/2023-04-26/22c25fc6b5b32280.jpg"
                        }
                        SingleImageItem()() {
                            itemImageUri =
                                "https://scpic.chinaz.net/files/default/imgs/2023-04-26/927afbe5af4fec22.jpg"
                        }
                    }
                }

                DslBaseEditItem()() {
                    itemEditText = this::class.java.simpleName
                    editItemConfig._lastEditSelectionStart = itemEditText?.length ?: -1
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
                    editItemConfig._lastEditSelectionStart = itemEditText?.length ?: -1

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

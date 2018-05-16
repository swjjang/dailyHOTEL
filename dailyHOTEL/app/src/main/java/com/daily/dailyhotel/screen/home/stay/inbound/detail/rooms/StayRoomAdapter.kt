package com.daily.dailyhotel.screen.home.stay.inbound.detail.rooms

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.StrikethroughSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.daily.base.util.DailyTextUtils
import com.daily.base.util.ExLog
import com.daily.base.util.FontManager
import com.daily.base.util.ScreenUtils
import com.daily.base.widget.DailyTextView
import com.daily.dailyhotel.entity.Room
import com.daily.dailyhotel.util.isNotNullAndNotEmpty
import com.daily.dailyhotel.util.isTextEmpty
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.ListRowStayRoomDataBinding
import com.twoheart.dailyhotel.place.base.OnBaseEventListener
import com.twoheart.dailyhotel.util.Util
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan

class StayRoomAdapter(private val context: Context, private val list: MutableList<Room>, private var nights: Int = 1) : RecyclerView.Adapter<StayRoomAdapter.RoomViewHolder>() {

    companion object {
        const val MENU_WIDTH_RATIO = 0.865f
    }

    enum class BedType(val type: String, val typeName: String = "체크인시 배정", val vectorIconResId: Int = R.drawable.vector_ic_detail_item_bed_double) {
        SINGLE("SINGLE", "싱글", R.drawable.vector_ic_detail_item_bed_single),
        DOUBLE("DOUBLE", "더블", R.drawable.vector_ic_detail_item_bed_double),
        SEMI_DOUBLE("SEMI_DOUBLE", "세미더블", R.drawable.vector_ic_detail_item_bed_double),
        KING("KING", "킹", R.drawable.vector_ic_detail_item_bed_double),
        QUEEN("QUEEN", "퀸", R.drawable.vector_ic_detail_item_bed_double),
        IN_FLOOR_HEATING("IN_FLOOR_HEATING", "온돌", R.drawable.vector_ic_detail_item_bed_heatingroom),
        ETC("ETC", "채크인시 배정", R.drawable.vector_ic_detail_item_bed_double)
    }

    private var onEventListener: OnEventListener? = null

    interface OnEventListener : OnBaseEventListener {
        fun onMoreImageClick(index: Int)
    }

    fun setEventListener(listener: OnEventListener?) {
        onEventListener = listener
    }

    fun getItem(position: Int): Room? {
        return (list.size > 0).let { list[position] }
    }

    fun setData(list: MutableList<Room>) {
        this.list.clear()
        this.list += list
    }

    fun setNights(nights: Int) {
        this.nights = nights
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val dataBinding = DataBindingUtil.inflate<ListRowStayRoomDataBinding>(
                LayoutInflater.from(parent.context), R.layout.list_row_stay_room_data, parent, false)

        dataBinding.roomLayout.layoutParams = RecyclerView.LayoutParams(getLayoutWidth().toInt(), RecyclerView.LayoutParams.MATCH_PARENT)

        return RoomViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val dataBinding: ListRowStayRoomDataBinding = holder.dataBinding

        val room = getItem(position) ?: return

        holder.dataBinding.root.setTag(R.id.blurView, holder.dataBinding.blurView)

        val margin = getLayoutMargin()
        (dataBinding.roomLayout.layoutParams as RecyclerView.LayoutParams).apply {
            when (position) {
                0 -> {
                    leftMargin = margin.toInt()
                    rightMargin = 0
                }

                itemCount - 1 -> {
                    leftMargin = 0
                    rightMargin = margin.toInt()
                }

                else -> {
                    leftMargin = 0
                    rightMargin = 0
                }
            }
        }

        if (room.imageInformation == null) {
            dataBinding.defaultImageLayout.visibility = View.GONE
            dataBinding.defaultImageLayout.setOnClickListener(null)
        } else {
            dataBinding.defaultImageLayout.visibility = View.VISIBLE
            dataBinding.simpleDraweeView.hierarchy.setPlaceholderImage(R.drawable.layerlist_placeholder)
            Util.requestImageResize(context, dataBinding.simpleDraweeView, room.imageInformation.imageMap.bigUrl)

            dataBinding.moreIconView.visibility = if (room.imageCount > 0) View.VISIBLE else View.GONE
            dataBinding.vrIconView.visibility = if (room.vrInformationList.isNotNullAndNotEmpty()) View.VISIBLE else View.GONE
        }

        dataBinding.roomNameTextView.text = room.name

        if (room.amountInformation == null) {
            dataBinding.discountPercentTextView.visibility = View.GONE
            dataBinding.priceTextView.visibility = View.GONE
            dataBinding.discountPriceTextView.setText(R.string.label_soldout)

            ExLog.e("amountInformation is null")
        } else {
            dataBinding.discountPercentTextView.visibility = View.VISIBLE
            dataBinding.priceTextView.visibility = View.VISIBLE

            val discountRateSpan = SpannableString("${room.amountInformation.discountRate}%")
            discountRateSpan.setSpan(CustomFontTypefaceSpan(FontManager.getInstance(context).regularTypeface), discountRateSpan.length - 1, discountRateSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            discountRateSpan.setSpan(AbsoluteSizeSpan(ScreenUtils.dpToPx(context, 12.0)), discountRateSpan.length - 1, discountRateSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            dataBinding.discountPercentTextView.text = discountRateSpan

            val nightsString = if (nights > 1) "/1박" else ""
            val discountPriceString = DailyTextUtils.getPriceFormat(context, room.amountInformation.discountAverage, false)

            val discountPriceSpan = SpannableString("$discountPriceString$nightsString")
            discountPriceSpan.setSpan(CustomFontTypefaceSpan(FontManager.getInstance(context).regularTypeface), discountPriceString.length, discountPriceSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            discountPriceSpan.setSpan(AbsoluteSizeSpan(ScreenUtils.dpToPx(context, 12.0)), discountPriceString.length - 1, discountPriceSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            dataBinding.discountPriceTextView.text = discountPriceSpan

            val priceSpan = SpannableString(DailyTextUtils.getPriceFormat(context, room.amountInformation.priceAverage, false))
            priceSpan.setSpan(StrikethroughSpan(), 0, priceSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            dataBinding.priceTextView.text = priceSpan
        }

        if (room.refundInformation == null || room.refundInformation.warningMessage.isTextEmpty()) {
            dataBinding.refundPolicyTextView.visibility = View.GONE
        } else {
            dataBinding.refundPolicyTextView.visibility = View.VISIBLE
            dataBinding.refundPolicyTextView.text = room.refundInformation.warningMessage
        }

        setBaseInformationGridView(dataBinding, room)
    }

    private fun setBaseInformationGridView(dataBinding: ListRowStayRoomDataBinding, room: Room) {
        val personsInformation: Room.PersonsInformation? = room.personsInformation
        val bedTypeList: List<Room.BedInformation.BedTypeInformation>? = room.bedInformation?.bedTypeList

        if (personsInformation == null && !bedTypeList.isNotNullAndNotEmpty() && room.squareMeter == 0f) {
            dataBinding.baseInfoGroup.visibility = View.GONE
            return
        }

        dataBinding.baseInfoGroup.visibility = View.VISIBLE

        setPersonInformationView(dataBinding, room)
        setBedInformationView(dataBinding, room)
        setSquareInformationView(dataBinding, room)
    }

    private fun setPersonInformationView(dataBinding: ListRowStayRoomDataBinding, room: Room) {
        val personsInformation: Room.PersonsInformation? = room.personsInformation

        var personVectorIconResId: Int = 0
        var personTitle: String = ""
        var personDescription: String = ""

        personsInformation?.let {
            personTitle = "${it.fixed}인 기준"

            personVectorIconResId = when (it.fixed) {
                0, 1 -> R.drawable.vector_ic_detail_item_people_1

                2 -> R.drawable.vector_ic_detail_item_people_2

                else -> R.drawable.vector_ic_detail_item_people_3
            }

            val subDescription = if (it.extra == 0) "" else (if (it.extraCharge) " (유료)" else " (무료)")
            personDescription = "최대 ${it.fixed + it.extra}인$subDescription"
        }

        dataBinding.personIconImageView.setVectorImageResource(personVectorIconResId)
        dataBinding.personTitleTextView.text = personTitle
        dataBinding.personDescriptionTextView.text = personDescription
    }

    private fun setBedInformationView(dataBinding: ListRowStayRoomDataBinding, room: Room) {
        val bedTypeList: List<Room.BedInformation.BedTypeInformation>? = room.bedInformation?.bedTypeList

        var bedVectorIconResId: Int = 0
        var bedDescription: String = ""

        bedTypeList?.let {
            val size = bedTypeList.size

            when (size) {
                0 -> {
                    bedVectorIconResId = BedType.ETC.vectorIconResId
                    bedDescription = "${BedType.ETC.typeName} 1개"
                }

                1 -> {
                    val count = bedTypeList[0].count
                    val type = bedTypeList[0].bedType.toUpperCase()
                    val bedType: BedType = try {
                        BedType.valueOf(type)
                    } catch (e: Exception) {
                        BedType.ETC
                    }

                    bedVectorIconResId = bedType.vectorIconResId
                    bedDescription = "${bedType.typeName} ${count}개"
                }

                else -> {
                    bedVectorIconResId = R.drawable.vector_ic_detail_item_bed_double

                    bedTypeList.forEachIndexed { index, bedTypeInformation ->
                        if (index in 1..(size - 1)) {
                            bedDescription += ", "
                        }

                        val bedType: BedType = try {
                            BedType.valueOf(bedTypeInformation.bedType.toUpperCase())
                        } catch (e: Exception) {
                            BedType.ETC
                        }

                        bedDescription += "${bedType.typeName} ${bedTypeInformation.count}"

                        if (index == size - 1) {
                            bedDescription += "개"
                        }
                    }
                }
            }
        }

        dataBinding.bedIconImageView.setVectorImageResource(bedVectorIconResId)
        dataBinding.firstDescriptionTextView.text = bedDescription

//        val firstLineEnd = dataBinding.firstDescriptionTextView.layout.getLineEnd(0)
//
//        ExLog.d("fullText Length : ${bedDescription.length} , firstLineEnd : ${firstLineEnd}")
    }

    private fun setSquareInformationView(dataBinding: ListRowStayRoomDataBinding, room: Room) {
        val squareMeterSpan = SpannableString("${room.squareMeter}m")
        squareMeterSpan.setSpan(AbsoluteSizeSpan(ScreenUtils.dpToPx(context, 5.0)), squareMeterSpan.length - 1, squareMeterSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        dataBinding.squareTitleTextView.text = squareMeterSpan

        val pyoung = Math.round(room.squareMeter / 400 * 121)
        dataBinding.squareDescriptionTextView.text = "${pyoung}평"
    }


    private fun getDescriptionTextView(showTopMargin: Boolean): DailyTextView {
        return DailyTextView(context).apply {
            setBackgroundResource(R.drawable.shape_rect_stay_room_grid_description_background)

            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                if (showTopMargin) topMargin = ScreenUtils.dpToPx(context, 2.0)

                setTextColor(context.resources.getColor(R.color.default_text_c929292))
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11f)
                gravity = Gravity.CENTER
                typeface = FontManager.getInstance(context).mediumTypeface
            }
        }
    }

    fun getLayoutWidth(): Float {
        return ScreenUtils.getScreenWidth(context) * MENU_WIDTH_RATIO
    }

    fun getLayoutMargin(): Float {
        return ScreenUtils.getScreenWidth(context) * (1.0f - MENU_WIDTH_RATIO) / 2.0f
    }

    inner class RoomViewHolder(val dataBinding: ListRowStayRoomDataBinding) : RecyclerView.ViewHolder(dataBinding.root)
}
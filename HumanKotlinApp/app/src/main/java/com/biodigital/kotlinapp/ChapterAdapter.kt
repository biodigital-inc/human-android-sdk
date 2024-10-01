package com.biodigital.kotlinapp


import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

import com.biodigital.humansdk.HKChapter

class ChapterAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private var chapters = ArrayList<HKChapter>(0)

    fun setChapters(chaps: Array<HKChapter>) {
        chapters.clear()
        chapters.addAll(chaps)
        println("got chapters")
        this.notifyDataSetChanged()
    }

    override fun getItem(i: Int): Fragment {
        val fragment = ChapterFragment()
        val args = Bundle()
        args.putString(ChapterFragment.ARG_TITLE, chapters[i].title)
        args.putString(ChapterFragment.ARG_DESC, chapters[i].description)
        fragment.arguments = args
        return fragment
    }

    override fun getCount(): Int {
        return chapters.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return chapters[position].title
    }


    class ChapterFragment : Fragment() {

        internal var expanded = false

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?, savedInstanceState: Bundle?
        ): View? {

            val rootView = inflater.inflate(R.layout.chapter_fragment, container, false)
            val args = arguments

            val title = args!!.getString(ARG_TITLE)
            val description = args.getString(ARG_DESC)
            println("got chapter title " + title!!)

            rootView.setBackgroundColor(Color.LTGRAY)
            rootView.background.alpha = 70

            val chapterTitleText = rootView.findViewById(R.id.chapterTitle) as TextView
            chapterTitleText.text = title
            val chapterDescText = rootView.findViewById(R.id.chapterDescription) as TextView
            chapterDescText.text = Html.fromHtml(description)
            chapterTitleText.movementMethod = ScrollingMovementMethod()
            chapterDescText.movementMethod = ScrollingMovementMethod()

            chapterTitleText.setOnClickListener { (activity as MainActivity).handleChapterClick() }
            return rootView
        }

        companion object {
            val ARG_TITLE = "title"
            val ARG_DESC = "description"
        }


    }
}

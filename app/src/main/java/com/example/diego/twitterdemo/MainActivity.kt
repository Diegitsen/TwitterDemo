 package com.example.diego.twitterdemo

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_ticket.view.*

 class MainActivity : AppCompatActivity() {


     var ListTweets=ArrayList<Ticket>()
     var adapter:MyTweetAdpater ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //data de prueba
        ListTweets.add(Ticket("0", "him", "url", "add"))
        ListTweets.add(Ticket("0", "him", "url", "add"))
        ListTweets.add(Ticket("0", "him", "url", "add"))
        ListTweets.add(Ticket("0", "him", "url", "add"))

        adapter = MyTweetAdpater(this, ListTweets)
        lvTweets.adapter = adapter

    }

    inner class  MyTweetAdpater: BaseAdapter {
        var tweetAdapter = ArrayList<Ticket>()
        var context: Context?=null
        constructor(context: Context, tweetAdpater: ArrayList<Ticket>):super(){
            this.tweetAdapter=tweetAdpater
            this.context=context
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {



            var mytweet=tweetAdapter[p0]

            if(mytweet.tweetPersonUID.equals("add")) {
                var myView = layoutInflater.inflate(R.layout.tweets_ticket, null)

                return myView

            }
            else{
                var myView=layoutInflater.inflate(R.layout.tweets_ticket,null)

                return myView
            }
        }

        override fun getItem(p0: Int): Any {
            return tweetAdapter[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {

            return tweetAdapter.size

        }

    }


}

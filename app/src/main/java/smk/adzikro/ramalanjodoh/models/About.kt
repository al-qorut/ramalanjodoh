package pst.adzikro.doadzikir.models

class About(var id :Int=0,
            var name:String="",
            var detail:String="",
            var web:String="")

class ListAbout(var about: About?,
                var isHeader:Boolean,
                var titleHeader:String?)
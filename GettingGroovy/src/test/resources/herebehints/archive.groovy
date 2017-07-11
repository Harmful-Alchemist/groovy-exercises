if (!session) {
    session = request.getSession(true)
}

if (!session.counter) {
    session.counter = 1
}

def added = params.add

def read = params.read

def clear= params.clear



html.html { // html is implicitly bound to new MarkupBuilder(out)
  head {
      title('Groovy Archive Servlet')
  }
  body {
    p("Hello, ${request.remoteHost}: ${session.counter}! ${new Date()}")

    if(added!=null&&clear==null){
      new File('persistin.dat') << added << System.getProperty("line.separator")
      p("added: ${added}")
    }

    if(read == 'true'&&clear==null){
      p("Entries:")
      new File('persistin.dat').eachLine {
        line ->
        p("entry: ${line}")
      }
    }
    
    if(clear!=null){ 
    	new File('persistin.dat').delete()
    	p("Deleted archive")
    }
  }
}
session.counter = session.counter + 1

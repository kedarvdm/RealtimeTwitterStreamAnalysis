var tabulate = function (data1,columns1) {
  var table = d3.select('#topUsers').html('').append('table')
	var thead = table.append('thead')
	var tbody = table.append('tbody')

	thead.append('tr')
	.selectAll('th')
    .data(columns1)
    .enter()
    .append('th')
	.text(function (d) { return d })

	var rows = tbody.selectAll('tr')
    .data(data1)
    .enter()
    .append('tr')

	var cells = rows.selectAll('td')
	    .data(function(row) {
	    	return columns1.map(function (column) {
	    		return { column: column, value: row[column] }
	      })
      })
      .enter()
    .append('td')
      .text(function (d) { return d.value })

  return table;
}

d3.csv('Visualization/RealTime/UserMentions/PopularUsers.csv',function (data1) {
    var columns1 = ['User','Count']
tabulate(data1,columns1)
})

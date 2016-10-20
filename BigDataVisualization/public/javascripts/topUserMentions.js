      //set up canvas and bar sizes
      var canvasWidth = 600,
          canvasHeight = 500,
          otherMargins = canvasWidth * 0.1,
          leftMargin = canvasWidth * 0.25,
          maxBarWidth = canvasHeight - - otherMargins - leftMargin
          maxChartHeight = canvasHeight - (otherMargins * 2);

      //set up linear scale for data to fit on chart area
      var xScale = d3.scale.linear()
                      .range([0, maxBarWidth]);

      //set up ordinal scale for x variables
      var yScale = d3.scale.ordinal();

      //add canvas to HTML
      var chart = d3.select("#graph").append("svg")
                                  .attr("width",canvasWidth)
                                  .attr("height", canvasHeight);

      //set up x axis
      var xAxis = d3.svg.axis()
                  .scale(xScale)
                  .orient("bottom")
                  .ticks(5);

      //set up y axis
      var yAxis = d3.svg.axis()
                  .scale(yScale)
                  .orient("left")
                  .tickSize(0);

      //add in data
      //csv has 4 lines before the data to be graphed
      //First line - Title of the chart
      //Second line - Source of the data
      //Third line - Metadata notes
      //Fourth line - blank
      //Fifth line - x variable name, y variable name
      //Sixth line onwards - x variable value, y variable value
        d3.xhr("Visualization/TopUserMentions/TopUserMentions.csv").get(function (error, response) {

          //retrieve data
          var dirtyCSV = response.responseText;
          var cleanCSV = dirtyCSV.split('\n').slice(4).join('\n');
          var data = d3.csv.parse(cleanCSV)

          //retrieve title
          var dirtyTitle = dirtyCSV.split('\n').slice(0,1).join('\n');
          var title = dirtyTitle.slice(0,-1);

              //get variable names
              var keys = d3.keys(data[0]);
              var namesTitle = keys[0];
              var valuesTitle = keys[1];

              //accessing the properties of each object with the variable name through its key
              var values = function(d) {return +d[valuesTitle];};
              var names = function(d) {return d[namesTitle];}

              // find highest value
              var maxValue = d3.max(data, values);

              //set y domain by mapping an array of the variables along x axis
              yScale.domain(data.map(names));

              //set x domain with max value and use .nice() to ensure the y axis is labelled above the max y value
              xScale.domain([0, maxValue]).nice();

        //set bar width with rangeBands ([x axis width], gap between bars, gap before and after bars) as a proportion of bar width
        yScale.rangeBands([0, maxChartHeight], 0.1, 0.25);

        //set up rectangle elements with attributes based on data
        var rects = chart.selectAll("rect")
                        .data(data)
                        .enter()
                        .append("rect");

        //set up attributes of svg rectangle elements based on attributes
        var rectAttributes = rects
                              .attr("x", leftMargin)
                              .attr("y", function (d) {return yScale(d[namesTitle]) + otherMargins; })
                              .attr("width", function (d) {return xScale(d[valuesTitle])})
                              .attr("height", yScale.rangeBand())
                              .attr("fill", "orange")
                              .on("mouseover", function(d, i) {

                                //change fill
                                d3.select(this)
                                      .attr("fill", "red");

                                //set up data to show on mouseover
                                var xPosition = (parseFloat(d3.select(this).attr("width")) + leftMargin + 6);
                                var yPosition = parseFloat(d3.select(this).attr("y")) + (parseFloat(d3.select(this).attr("height")) / 2);
                                chart.append("text")
                                      .attr("id", "tooltip")
                                      .attr("x", xPosition)
                                      .attr("y", yPosition)
                                      .attr("dy", "0.35em")
                                      .attr("text-anchor", "start")
                                      .attr("font-family", "sans-serif")
                                      .attr("font-size", "12px")
                                      .attr("font-weight", "bold")
                                      .attr("fill", "black")
                                      .text(d[valuesTitle]);
                              })

                              //transition out
                              .on("mouseout", function(d) {
                                d3.select(this)
                                  .transition()
                                  .duration(250)
                                  .attr("fill", "orange");
                                d3.select("#tooltip").remove();
                              });

        //chart title
        chart.append("text")
              .attr("x", canvasWidth / 2)
              .attr("y", otherMargins / 2)
              .attr("dy", "0.35em")
              .attr("text-anchor", "middle")
              .attr("font-family", "sans-serif")
              .attr("font-size", "20px")
              .attr("font-weight", "bold")
              .attr("fill", "blue")
              .text(title);

        //append x axis
        chart.append("g")
              .attr("transform", "translate(" + leftMargin + ", " + (maxChartHeight + otherMargins) + ")")
              .attr("text-anchor", "middle")
              .attr("font-family", "sans-serif")
              .attr("font-size", "10px")
              .style("stroke", "black")
              .style("fill", "none")
              .style("stroke-width", 1)
              .style("shape-rendering", "crispEdges")
              .call(xAxis)
                .selectAll("text")
                .attr("stroke", "none")
                .attr("fill", "black");

        //append y axis
        chart.append("g")
              .attr("transform", "translate(" + leftMargin + ", " + otherMargins + ")")
              .attr("text-anchor", "middle")
              .attr("font-family", "sans-serif")
              .attr("font-size", "10px")
              .attr("font-weight", "bold")
              .style("stroke", "black")
              .style("fill", "none")
              .style("stroke-width", 1)
              .style("shape-rendering", "crispEdges")
              .call(yAxis)
              .selectAll("text")
              .attr("dx", "-1.15em")
              .attr("stroke", "none")
              .attr("fill", "black")

          //x axis title
          chart.append("text")
                .attr("x", (maxBarWidth / 2) + leftMargin)
                .attr("y", canvasHeight - (otherMargins / 3))
                .attr("text-anchor", "middle")
                .attr("font-family", "sans-serif")
                .attr("font-size", "14px")
                .attr("font-weight", "bold")
                .attr("fill", "red")
                .text(valuesTitle);

          //y axis title
          chart.append("text")
                .attr("transform", "rotate(-90)")
                .attr("x", -((maxChartHeight / 2) + otherMargins))
                .attr("y", leftMargin / 4)
                .attr("text-anchor", "middle")
                .attr("font-family", "sans-serif")
                .attr("font-size", "14px")
                .attr("font-weight", "bold")
                .attr("fill", "red")
                .text(namesTitle);

        //chart border - not necessary used for reference for the edge of canvas
        var border = chart.append("rect")
                          .attr("x", 0)
                          .attr("y", 0)
                          .attr("height", canvasHeight)
                          .attr("width", canvasWidth)
                          .style("stroke", "black")
                          .style("fill", "none")
                          .style("stroke-width", 1);

        //log anything in the console for debugging
        //console.log(yAxis);

      });

      //line wrap function adapted from "Wrapping Long Labels" - mike bostock
      function wrap(text, width) {
        text.each(function() {
          var text = d3.select(this),
            words = text.text().split(/\s+/).reverse(),
            word,
            line = [],
            lineNumber = 0,
            lineHeight = 1.1, //em
            y = text.attr("y"),
            dy = parseFloat(text.attr("dy")),
            tspan = text.text(null).append("tspan").attr("x", 0).attr("y", y).attr("dy", dy + "em");
          while (word = words.pop()) {
            line.push(word);
            tspan.text(line.join(" "));
            if (tspan.node().getComputedTextLength() > width) {
              line.pop();
              tspan.text(line.join(" "));
              line = [word];
              tspan = text.append("tspan").attr("x", 0).attr("y", y).attr("dy", ++lineNumber * lineHeight + dy + "em").text(word);
            }
          }
        })
      }

      //while the data is being loaded it turns the strings into a number
      function type(d) {
        d[yName] = +d[yName];
        return d;

      }
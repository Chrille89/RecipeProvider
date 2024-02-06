const fs = require("fs");
const PDFParser = require("pdf2json");
const decode = require('urldecode')

const pdfParser = new PDFParser(this,1);

pdfParser.on("pdfParser_dataError", errData => console.error(errData.parserError) );
pdfParser.on("pdfParser_dataReady", pdfData => {
    let texts = JSON.parse(JSON.stringify(pdfData)).Pages[0].Texts;
    const regex = new RegExp('[A-Za-z]');

    // title
    var title = "";
    const filtered = texts
    .filter((t)=> regex.test(decode(t.R[0].T)))
    .map((t)=> decode(t.R[0].T))
    .slice(0, 4)
    .forEach((element,index) => {
       if(index === 1)  title+= element+", ";
       else title+= element;
    });

    //zubereitung
    var preparationStr = "";
    texts = JSON.parse(JSON.stringify(pdfData)).Pages[1].Texts;
    texts
    .filter((t)=> t.R[0].S === -1)
    .map((t)=> decode(t.R[0].T))
    .filter((t)=> t !== "*")
    .forEach((element,index) => {    
        preparationStr+= element + " ";
    });
    const preparationArray = preparationStr
    .replaceAll("ca.","")
    .replaceAll("Min .","Minuten")
    .slice(0,preparationStr.indexOf("Guten Appetit!")).split(".")

    //zutaten
    var ingredients = JSON.parse(JSON.stringify(pdfData)).Pages[1].Texts;
    ingredients = ingredients
    .filter((t)=> t.R[0].S === 2)
    .map((t)=> decode(t.R[0].T))
    .filter((t)=> t !== "*")
    .filter((t)=> t !== "(Drillinge)")
    .filter((t)=> t !== " | ")
    .filter((t)=> t !== "„Hello  ")
    .filter((t)=> t !== "Paprika“")
    ingredients = ingredients.slice(ingredients.indexOf("4P")+1,ingredients.indexOf("Portion")-1);
    ingredients = ingredients.map((element,index) => {
    console.log(element);
        if(index === 0) {
            return element + " "+ingredients[1];
        } else if(index % 4 === 0) {
            return element + " "+ingredients[index +1];
        }
    }).filter((element) => element !== undefined)

    const requestDto = {
        "title": title,
        "persons": 2,
        "ingredients": ingredients,
        "preparation": preparationArray
        
    }

    console.log("request: ",requestDto);
  fs.writeFile("./test.json", JSON.stringify(requestDto), ()=>{console.log("Done.");});
});

pdfParser.loadPDF("./test.pdf");
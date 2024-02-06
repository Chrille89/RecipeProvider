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
    .map((t)=> decode(t.R[0].T).replace("„Hello",""))
    .filter((t)=> t !== "*")
    .filter((t)=> t !== "(Drillinge)")
    .filter((t)=> t !== " | ")
    .filter((t)=> t !== "„Hello ")
    .filter((t)=> t !== "Paprika“")
    ingredients = ingredients.slice(ingredients.indexOf("4P")+1,ingredients.indexOf("Portion")-1);

    let ingredientsPersons = {};
    let i =0;
    while( i < ingredients.length) {
        ingredientsPersons[ingredients[i].trim()]= {
            persons: {
                2: {
                    amount: ingredients[i+1]
                },
                3: {
                    amount: ingredients[i+2]
                },
                4: {
                    amount: ingredients[i+3]
                }
            }
        }
        i+=4;
    }

    const requestDto = {
        "title": title,
        "ingredients": ingredientsPersons,
        "preparation": preparationArray

    }

    console.log("request: ",requestDto);
  fs.writeFile("./test.json", JSON.stringify(requestDto), ()=>{console.log("Done.");});
});

pdfParser.loadPDF("./test.pdf");
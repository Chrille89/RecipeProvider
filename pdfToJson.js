const fs = require("fs");
const PDFParser = require("pdf2json");
const decode = require('urldecode')
const request = require('sync-request');
const URL = "http://h2857701.stratoserver.net:8080/RecipeProvider/recipes";
var fileNames = [];
var fileFolder = 'recipes/';
var fileCont = 0;

var loadPDF = function (filePath) {
  if (fileNames.length === fileCont) {
    //Insert in db and add any FINAL code, then return;
  }
  else {
    //Call for another file to process
    var pdfParser = null;
    pdfParser = new PDFParser();
    pdfParser.loadPDF(filePath);

    pdfParser.on('pdfParser_dataError', function (err) {
      console.error(errData.parserError)
    });

    pdfParser.on('pdfParser_dataReady', function (pdfData) {
      let texts = JSON.parse(JSON.stringify(pdfData)).Pages[0].Texts;
      const regexCharsNumbers = new RegExp('[A-Za-z0-9]');
      const regexChars = new RegExp('[A-Za-z]');
      const regexNumbers = new RegExp('[0-9]');

      // title
      var title = "";
      const filtered = texts
        .filter((t) => {
          return t.R[0].S === -1 && t.R[0].T !== "%20%20";
        })
        .map((t) => {
          return decode(t.R[0].T);
        })
        .forEach((element, index) => {
          if (regexChars.test(element)) {
            if (index === 1) title += element + ", ";
            else title += element;
          }
        });

      //zubereitung
      var preparationStr = "";
      var ignoreFlag = false;
      texts = JSON.parse(JSON.stringify(pdfData)).Pages[1].Texts;
      texts
        .filter((t) => t.R[0].S === -1)
        .map((t) => decode(t.R[0].T))
        .filter((t) => t !== "*")
        .forEach((element, index) => {
          preparationStr += element + " ";
        });
      const preparationArray = preparationStr
        .replaceAll("ca.", "")
        .replaceAll("Min .", "Minuten")
        .replaceAll(" EL ", "Esslöffel")
        .replaceAll(" TL ", "Teelöffel")
        .replaceAll(" Sek ", "Sekunden")
        .replaceAll(" –  ", " bis ")
        .replaceAll(" g ", " Gramm ")
        .replaceAll(" cm ", " Zentimeter ")
        .replaceAll(" ml ", " Milliliter ")
        .slice(0, preparationStr.indexOf("Guten Appetit!")).split(".")

      //zutaten
      var ingredients = JSON.parse(JSON.stringify(pdfData)).Pages[1].Texts;
      ingredients = ingredients
        .filter((t) => t.R[0].S === 2)
        .map((t) => decode(t.R[0].T).replace("„Hello", ""))
        .filter((t) => t !== "*")
        .filter((t) => t !== "(Drillinge)")
        .filter((t) => t !== "(Scheiben)")
        .filter((t) => t !== " | ")
        .filter((t) => t !== "„Hello ")
        .filter((t) => t !== "Paprika“")
      ingredients = ingredients
        .slice(ingredients.indexOf("4P") + 1, ingredients.indexOf("Portion") - 1)
        .filter((i) => regexCharsNumbers.test(i))

      let ingredientsPersons = [];
      let i = 0;
      while (i < ingredients.length) {
        let ingredientName = ingredients[i];
        let ingredientAmount = ingredients[i + 1];
        if (ingredientAmount) {
          if (ingredientAmount.split(" ").length != 2 && !regexNumbers.test(ingredientAmount.split(" ")[0])) {
            i++;
            continue;
          }
        }
        let ingredient = {
          name: ingredients[i],
          persons: {
            2: {
              amount: ingredients[i + 1]
            },
            3: {
              amount: ingredients[i + 2]
            },
            4: {
              amount: ingredients[i + 3]
            }
          }
        }
        ingredientsPersons.push(ingredient);
        i += 4;
      }

      const recipeWriteDto = {
        "title": title,
        "ingredients": ingredientsPersons,
        "preparation": preparationArray

      }

      var res = request('POST', URL, {
        json: recipeWriteDto,
      });
      if (res.statusCode == 204) {
        console.log("Request successfully.");
      } else {
        console.log("Request failed: ", res)
      }
      fileCont++; //increase the file counter
      loadPDF(fileFolder + fileNames[fileCont]); //parse the next file
    });
  }
};

fs.readdir(fileFolder, function (err, files) {
  for (var i = files.length - 1; i >= 0; i--) {
    if (files[i].indexOf('.pdf') !== -1) {
      fileNames.push(files[i]);
    }
  }

  loadPDF(fileFolder + fileNames[fileCont]);
});
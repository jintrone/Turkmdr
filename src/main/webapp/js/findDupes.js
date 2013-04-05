function findDupes(tokens, maxdupes) {
    console.log(tokens);
    var tpos = {};
    var dupcount = [];
    for (var i = 0; i < tokens.length && dupcount.length < maxdupes; i++) {
        if (!tpos[tokens[i]]) {
            tpos[tokens[i]] = [i];
            dupcount = [];
        } else {
            tpos[tokens[i]].push(i);
            if (dupcount.length > 0) {
                var found = false;
                for (var j = 0; j < tpos[tokens[i]].length - 1; j++) {
                    console.log("Compare "+tokens[tpos[tokens[j]] - 1]+" to "+tokens[i - 1]);
                    if (tokens[tpos[tokens[i]][j] - 1] == tokens[i - 1]) {
                        dupcount.push(tokens[i]);
                        console.log(JSON.stringify(dupcount));
                        found = true;
                        break;
                    }
                }
                if (!found) dupcount = [tokens[i]];
            } else {
                dupcount = [tokens[i]];
            }


        }

    }

    return dupcount;

}

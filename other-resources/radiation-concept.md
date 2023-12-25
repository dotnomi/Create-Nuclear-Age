# Radiation Concept

>Radiation is one of the main components of the mod and without it, it definitely wouldn't be as interesting as it is. It changes the entire gameplay from mining to life itself. 

### How is it implemented?

#### Measurements:

> There is a new measuring unit for the radiation called `RU/t` which means `Radiation Units per Tick`.
> There is also a basic unit for the radiation dose called `RU`.

#### Block and Item Behaviour:

> Blocks and items that are radioactive emit various doses of `RU/t`.
> This means that if you are nearby of radioactive blocks/items you will absorb a certain dose of `RU` in a certain time.
 
#### Gameplay:

#### Background radiation:

> ### Overworld:
> > Background radiation of `0 RU/t` above ground and `1-2 RU/t` in caves.
> ### Nether:
> > Background radiation of `3-5 RU/t`. Depends on the biom in which you are located.
> ### The End:
> > No background radiation.
 
#### Stages of radiation:

> **Deadly dose:** `100,000 RU`
> ### Stage 0 (Very Low radiation dose)
> > Range: `0-34,999 RU`\
> > Effect: `None`\
> > Damage: `None`\
> > Time until death: `infinit`
> ### Stage 1 (Low radiation dose)
> > Range: `35,000-74,999 RU`\
> > Effect: `Radiation I`\
> > Damage: `1/64 hearts per 5 ticks`\
> > Time until death: `160 seconds`
> ### Stage 2 (High radiation dose)
> > Range: `75,000-99,999 RU`\
> > Effect: `Radiation II`\
> > Damage: `1/16 hearts per 5 ticks`\
> > Time until death: `40 seconds`
> ### Stage 3 (Very high radiation dose - deadly)
> > Range: `100,000 - infinit RU`\
> > Effect: `Radiation III`\
> > Damage: `1/2 hearts per 5 ticks`\
> > Time until death: `2.5 seconds`



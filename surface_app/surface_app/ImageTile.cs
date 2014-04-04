using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Media;

namespace surface_app
{

    class ImageTile : System.Windows.Shapes.Shape
    {
        #region Private Stuff

        private HashSet<int> tiles;
        private VisualBrush tileBrush;
        private Geometry tileShape;

        #endregion

        #region Public Properties

        public HashSet<int> Tiles
        {
            get
            {
                return tiles;
            }
        }

        public Geometry TileShape
        {
            get
            {
                return tileShape;
            }
        }

        public VisualBrush TileBrush
        {
            get
            {
                return tileBrush;
            }
        }

        #endregion

        #region Initialization

        public ImageTile(int id, Geometry shape, VisualBrush brush)
        {
            tileShape = shape;
            tileBrush = brush;
        }

        #endregion

    }
}
